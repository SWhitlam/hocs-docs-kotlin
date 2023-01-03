package uk.gov.digital.ho.hocs.document.client.auditclient

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.MessageAttributeValue
import aws.sdk.kotlin.services.sns.publish
import com.fasterxml.jackson.databind.ObjectMapper
import net.logstash.logback.argument.StructuredArguments.value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uk.gov.digital.ho.hocs.document.application.LogEvent
import uk.gov.digital.ho.hocs.document.application.LogEvent.*
import uk.gov.digital.ho.hocs.document.application.RequestDataFilter.Companion.CORRELATION_ID_HEADER
import uk.gov.digital.ho.hocs.document.application.RequestDataFilter.Companion.USER_ID_HEADER
import uk.gov.digital.ho.hocs.document.client.auditclient.dto.AuditPayload
import uk.gov.digital.ho.hocs.document.client.auditclient.dto.CreateAuditRequest
import uk.gov.digital.ho.hocs.document.model.DocumentData
import java.time.LocalDateTime
import java.util.*

@Service
class AuditClient(
    private val auditSearchSnsClient: SnsClient,
    @Value("\${aws.sns.audit-search.arn}") val auditQueue: String,
    @Value("\${auditing.deployment.name}") val raisingService: String,
    @Value("\${auditing.deployment.namespace}") val namespace: String,
    val objectMapper: ObjectMapper) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_TYPE_HEADER = "event_type"
    }

    suspend fun createDocumentAudit(documentData: DocumentData) {
        val request = generateAuditRequest(
            documentData.externalReferenceUUID,
            AuditPayload(documentData.uuid, documentData.type, documentData.displayName),
            EventType.DOCUMENT_CREATED.name)

        auditSearchSnsClient.publish {
                topicArn = auditQueue
                message = objectMapper.writeValueAsString(request)
                messageAttributes = getQueueHeaders(EventType.DOCUMENT_CREATED.toString())
            }.runCatching {
                log.info("Audit message sent to queue with message Id $messageId for document ${documentData.uuid}", value(LogEvent.EVENT, AUDIT_EVENT_CREATED.name))
            }.onFailure { e ->
                log.error("Error sending audit message to queue for document ${documentData.uuid}", e, value(LogEvent.EVENT, AUDIT_FAILED.name))
            }
        }


    suspend fun createDocumentsAudit(documentData: Collection<DocumentData>) = documentData.forEach { document -> createDocumentAudit(document) }

//
//    fun updateDocumentAudit(documentData: DocumentData) {
//        val request: CreateAuditRequest = generateAuditRequest(documentData.getExternalReferenceUUID(),
//                createAuditPayload(documentData),
//                EventType.DOCUMENT_UPDATED.toString())
//        try {
//            producerTemplate.sendBodyAndHeaders(auditQueue, objectMapper.writeValueAsString(request), getQueueHeaders(EventType.DOCUMENT_CREATED.toString()))
//            log.info("Auditing 'Update Document', document UUID: {}, case UUID: {}, correlationID: {}, UserID: {}",
//                    documentData.getUuid(),
//                    documentData.getExternalReferenceUUID(),
//                    requestData.correlationId(),
//                    requestData.userId(),
//                    value(LogEvent.EVENT, LogEvent.AUDIT_EVENT_CREATED))
//        } catch (e: Exception) {
//            logError(e, documentData.getUuid())
//        }
//    }
//
//    fun deleteDocumentAudit(documentData: DocumentData) {
//        val request: CreateAuditRequest = generateAuditRequest(documentData.getExternalReferenceUUID(),
//                createAuditPayload(documentData),
//                EventType.DOCUMENT_DELETED.toString())
//        try {
//            producerTemplate.sendBodyAndHeaders(auditQueue, objectMapper.writeValueAsString(request), getQueueHeaders(EventType.DOCUMENT_DELETED.toString()))
//            log.info("Auditing 'Delete Document', document UUID: {}, case UUID: {}, correlationID: {}, UserID: {}",
//                    documentData.getUuid(),
//                    documentData.getExternalReferenceUUID(),
//                    requestData.correlationId(),
//                    requestData.userId(),
//                    value(LogEvent.EVENT, LogEvent.AUDIT_EVENT_CREATED))
//        } catch (e: Exception) {
//            logError(e, documentData.getUuid())
//        }
//    }


private fun generateAuditRequest(caseUUID: UUID, auditPayload: AuditPayload, eventType: String): CreateAuditRequest {
    return CreateAuditRequest(
        UUID.randomUUID().toString(), // TODO: get from context
        caseUUID,
        raisingService,
        auditPayload,
        namespace,
        LocalDateTime.now(),
        eventType,
        UUID.randomUUID().toString()) // TODO: get from context
}

private fun getQueueHeaders(eventType: String): Map<String, MessageAttributeValue> {
    return mapOf(
        EVENT_TYPE_HEADER to MessageAttributeValue {
            stringValue = eventType
            dataType = "String"
        },
        CORRELATION_ID_HEADER to MessageAttributeValue {
            stringValue = UUID.randomUUID().toString() // TODO: get from context
            dataType = "String"
        },
        USER_ID_HEADER to MessageAttributeValue {
            stringValue = UUID.randomUUID().toString() // TODO: get from context
            dataType = "String"
        },
    )
}

}