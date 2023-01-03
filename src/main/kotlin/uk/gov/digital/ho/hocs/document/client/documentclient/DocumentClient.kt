package uk.gov.digital.ho.hocs.document.client.documentclient

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uk.gov.digital.ho.hocs.document.application.LogEvent
import uk.gov.digital.ho.hocs.document.application.RequestDataFilter
import java.util.UUID

@Service
class DocumentClient(
    private val documentClient: SqsClient,
    @Value("\${aws.sqs.document.url}") val documentQueue: String,
    private val objectMapper: ObjectMapper
) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun sendDocumentRequest(uuid: UUID, fileLink: String, convertTo: String, externalReferenceUUID: UUID) {
        val request = SendMessageRequest {
            queueUrl = documentQueue
            messageBody = objectMapper.writeValueAsString(ProcessDocumentRequest(uuid, fileLink, convertTo, externalReferenceUUID))
            messageAttributes = getQueueHeaders()
        }

        documentClient.sendMessage(request).runCatching {
                log.debug("Document message sent to queue with message Id $messageId for document $uuid", StructuredArguments.value(LogEvent.EVENT, LogEvent.AUDIT_EVENT_CREATED.name))
            }.onFailure { e ->
                log.error("Error sending document message to queue for document $uuid", e, StructuredArguments.value(LogEvent.EVENT, LogEvent.DOCUMENT_CLIENT_FAILURE))
            }
        }


    private fun getQueueHeaders(): Map<String, MessageAttributeValue> {

        return mapOf(
            RequestDataFilter.CORRELATION_ID_HEADER to MessageAttributeValue {
                stringValue = UUID.randomUUID().toString() // TODO: get from context
                dataType = "String"
            },
            RequestDataFilter.USER_ID_HEADER to MessageAttributeValue {
                stringValue = UUID.randomUUID().toString() // TODO: get from context
                dataType = "String"
            })
    }

    class ProcessDocumentRequest (val uuid: UUID, val fileLink: String, val convertTo: String, val externalReferenceUUID: UUID)

}