package uk.gov.digital.ho.hocs.document.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.digital.ho.hocs.document.api.dto.CreateDocumentRequest
import uk.gov.digital.ho.hocs.document.service.DocumentService
import java.util.UUID


@RestController
@RequestMapping("/document")
class DocumentResource(
        private val documentService: DocumentService
) {

    @PostMapping
    suspend fun createDocument(@RequestBody createDocumentRequest: CreateDocumentRequest) : UUID? {
        return documentService.createDocument(createDocumentRequest)
    }
}