package uk.gov.digital.ho.hocs.document.client.auditclient.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class CreateAuditRequest (
    @JsonProperty(value = "correlation_id")
    private val correlationID: String? = null,

    @JsonProperty(value = "caseUUID")
    private val caseUUID: UUID? = null,

    @JsonProperty(value = "raising_service")
    private val raisingService: String? = null,

    @JsonProperty(value = "audit_payload")
    private val auditPayload: AuditPayload? = null,

    @JsonProperty(value = "namespace")
    private val namespace: String? = null,

    @JsonProperty(value = "audit_timestamp")
    private val auditTimestamp: LocalDateTime? = null,

    @JsonProperty(value = "type")
    private val type: String? = null,

    @JsonProperty(value = "user_id")
    private val userID: String? = null
)

class AuditPayload(
        val documentUUID: UUID,
        val documentType: String,
        val documentTitle: String
)