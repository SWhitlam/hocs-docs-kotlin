package uk.gov.digital.ho.hocs.document.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.digital.ho.hocs.document.api.dto.CreateDocumentRequest
import uk.gov.digital.ho.hocs.document.application.LogEvent
import uk.gov.digital.ho.hocs.document.application.exception.EntityNotFoundException
import uk.gov.digital.ho.hocs.document.client.auditclient.AuditClient
import uk.gov.digital.ho.hocs.document.client.documentclient.DocumentClient
import uk.gov.digital.ho.hocs.document.model.DocumentData
import uk.gov.digital.ho.hocs.document.model.DocumentData.DocumentStatus.PENDING
import uk.gov.digital.ho.hocs.document.repository.DocumentRepository
import java.util.*

@Service
class DocumentService(
        private val documentRepository: DocumentRepository,
        private val auditClient: AuditClient,
        private val documentClient: DocumentClient
) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    suspend fun createDocument(createDocumentRequest: CreateDocumentRequest): UUID {
        log.info("Creating document with name: ${createDocumentRequest.name}, case UUID: ${createDocumentRequest.externalReferenceUUID}")

        val createDocumentData = DocumentData(
                uuid = UUID.randomUUID(),
                externalReferenceUUID = createDocumentRequest.externalReferenceUUID,
                displayName = createDocumentRequest.name,
                fileLink = createDocumentRequest.fileLink,
                type = createDocumentRequest.type,
                uploadOwnerUUID = UUID.randomUUID(), //TODO:: get from context
                status = PENDING.name)

        val documentData = documentRepository.save(createDocumentData)
        documentClient.sendDocumentRequest(documentData.uuid, documentData.fileLink, createDocumentRequest.convertTo, createDocumentRequest.externalReferenceUUID)
        auditClient.createDocumentAudit(documentData)

        return documentData.uuid.also {
            log.info("Created document with name: ${createDocumentRequest.name}, case UUID: ${createDocumentRequest.externalReferenceUUID}, document UUID: $it")
        }
    }

    @Transactional
    suspend fun updateDocumentStatus(documentUUID: UUID, status: DocumentData.DocumentStatus, fileLink: String, pdfLink: String?) {
        log.info("Updating document status for document UUID: $documentUUID, status: $status")
        val documentData = getDocumentData(documentUUID)
        documentData.status = status.name
        documentData.fileLink = fileLink
        documentData.pdfLink = pdfLink
        documentRepository.save(documentData)
        log.info("Updated document status for document UUID: $documentUUID, status: $status")
    }

    private suspend fun getDocumentData(documentUUID: UUID): DocumentData {
        val documentData = documentRepository.findByUuid(documentUUID)
        return documentData
            ?: throw EntityNotFoundException("Document UUID: $documentUUID not found!", LogEvent.DOCUMENT_NOT_FOUND)
    }
}
