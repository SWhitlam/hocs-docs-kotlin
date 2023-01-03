package uk.gov.digital.ho.hocs.document.client.documentclient

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ChangeMessageVisibilityRequest
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.Message
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import uk.gov.digital.ho.hocs.document.application.LogEvent
import uk.gov.digital.ho.hocs.document.application.exception.MalwareCheckException
import uk.gov.digital.ho.hocs.document.service.S3Service
import java.lang.Thread.currentThread
import java.util.UUID
import javax.annotation.PreDestroy
import kotlin.coroutines.CoroutineContext

@Component
class DocumentListener(private val documentClient: SqsClient,
                       @Value("\${aws.sqs.document.url}") val documentQueue: String,
                       private val objectMapper: ObjectMapper,
                       private val s3Service: S3Service,
                       @Qualifier("clamav") private val clamAvClient: WebClient,
                       @Qualifier("converter") private val converterClient: WebClient
) : CoroutineScope, CommandLineRunner {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob
    private val supervisorJob = SupervisorJob()

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PreDestroy
    fun destroy() {
        supervisorJob.cancel()
    }

    fun start() = launch {
        val messageChannel = Channel<Message>()
        repeat(10) { launchWorker(messageChannel) }
        launchMsgReceiver(messageChannel)
    }

    fun CoroutineScope.launchMsgReceiver(messageChannel: Channel<Message>) = launch {
        repeatUntilCancelled {
            val receiveMessageRequest = ReceiveMessageRequest {
                queueUrl = documentQueue
                maxNumberOfMessages = 10
                visibilityTimeout = 120
                waitTimeSeconds = 20
            }
            documentClient.receiveMessage(receiveMessageRequest).runCatching {
                messages?.forEach { message ->
                    messageChannel.send(message)
                }
            }.onFailure { e ->
                log.error("Error receiving message from queue", e, StructuredArguments.value(LogEvent.EVENT, LogEvent.DOCUMENT_CLIENT_FAILURE))
            }
        }
    }

    private fun CoroutineScope.launchWorker(channel: ReceiveChannel<Message>) = launch {
        repeatUntilCancelled {
            for (msg in channel) {
                try {
                    processMsg(msg)
                    deleteMessage(msg)
                } catch (ex: Exception) {
                    println("${currentThread().name} exception trying to process message ${msg.body}")
                    ex.printStackTrace()
                    changeVisibility(msg)
                }
            }
        }
    }

    private suspend fun processMsg(message: Message) {
        log.debug("${currentThread().name} Started processing message: ${message.messageId}")

        val documentMsg = objectMapper.readValue(message.body, DocumentClient.ProcessDocumentRequest::class.java)
        val byteArrayBody = s3Service.getUnTrustedObject(documentMsg.fileLink)
        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder.part("file", byteArrayBody).filename(documentMsg.fileLink)

        if(!scanDocument(multipartBodyBuilder)) {
            log.error("Document failed scan: ${documentMsg.fileLink}", StructuredArguments.value(LogEvent.EVENT, LogEvent.DOCUMENT_VIRUS_SCAN_FAILURE))
            throw MalwareCheckException("Document failed scan: ${documentMsg.fileLink}", LogEvent.DOCUMENT_VIRUS_SCAN_FAILURE)
        }

        val destinationObjectKey = "${documentMsg.externalReferenceUUID}/${UUID.randomUUID()}.${documentMsg.fileLink.substringAfterLast('.', "")}"
        s3Service.putTrustedObject(destinationObjectKey, byteArrayBody).also {
            log.info("${currentThread().name} Uploaded original file to trusted bucket with key $destinationObjectKey")
        }

        val conversionResult = convertDocument(multipartBodyBuilder).also {
            log.info("${currentThread().name} Document conversion complete for document $documentMsg.uuid")
        }

        val convertedObjectKey = "${documentMsg.externalReferenceUUID}/${UUID.randomUUID()}.${documentMsg.convertTo.lowercase()}"
        s3Service.putTrustedObject(convertedObjectKey, conversionResult).also {
            log.info("${currentThread().name} Uploaded converted file to trusted bucket with key $destinationObjectKey")
        }
    }

    private suspend fun scanDocument(multipartBodyBuilder: MultipartBodyBuilder): Boolean {
        return clamAvClient.post()
                .uri("/scan")
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .awaitBody<String>().contains("Everything ok : true")
    }

    private suspend fun convertDocument(multipartBodyBuilder: MultipartBodyBuilder): ByteArray {
        return converterClient.post()
            .uri("/convert")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .retrieve()
            .awaitBody()
    }

    private suspend fun deleteMessage(message: Message) {
        documentClient.deleteMessage(DeleteMessageRequest {
            queueUrl = documentQueue
            receiptHandle = message.receiptHandle

        })
        log.debug("${currentThread().name} Message deleted: ${message.messageId}")
    }

    private suspend fun changeVisibility(message: Message) {
        documentClient.changeMessageVisibility(ChangeMessageVisibilityRequest {
            queueUrl = documentQueue
            receiptHandle = message.receiptHandle
            visibilityTimeout = 10
        })
        log.debug("${currentThread().name} Changed visibility of message: ${message.messageId}")
    }

    suspend fun CoroutineScope.repeatUntilCancelled(block: suspend () -> Unit) {
        while (isActive) {
            block()
            yield()
        }
        log.debug("Coroutine on ${currentThread().name} exiting")
    }

    override fun run(vararg args: String?) {
        start()
    }


}