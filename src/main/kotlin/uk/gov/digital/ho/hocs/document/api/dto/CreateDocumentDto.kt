package uk.gov.digital.ho.hocs.document.api.dto

import java.util.UUID

class CreateDocumentRequest(
        val externalReferenceUUID: UUID,
        val name: String,
        val fileLink: String,
        val type: String,
        val convertTo: String = "PDF")