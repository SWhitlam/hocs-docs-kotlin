package uk.gov.digital.ho.hocs.document.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID
import kotlin.String

@Table("document_data")
class DocumentData(
    @Id
    @Column("id")
    var id: Long? = null,

    @Column("uuid")
    val uuid: UUID,

    @Column("external_reference_uuid")
    val externalReferenceUUID: UUID,

    @Column("type")
    val type: String,

    @Column("display_name")
    val displayName: String,

    @Column("file_link")
    var fileLink: String,

    @Column("pdf_link")
    var pdfLink: String? = null,

    @Column("status")
    var status: String = DocumentStatus.PENDING.name,

    @Column("created_on")
    val created: LocalDateTime = LocalDateTime.now(),

    @Column("updated_on")
    var updated: LocalDateTime? = null,

    @Column("deleted")
    var deleted: Boolean = false,

    @Column("upload_owner")
    val uploadOwnerUUID: UUID,

    @Column("deleted_on")
    var deletedOn: LocalDateTime? = null
)
{

    fun update(fileLink: String, pdfLink: String, status: DocumentStatus) {
        this.fileLink = fileLink
        this.pdfLink = pdfLink
        this.status = status.toString()
        updated = LocalDateTime.now()
    }

    fun setDeleted() {
        deleted = true
        deletedOn = LocalDateTime.now()
    }

    enum class DocumentStatus (val value: String? = null) {
        PENDING("Pending"), UPLOADED("Uploaded"), FAILED_VIRUS("Failed Virus Scan"), FAILED_CONVERSION("Failed PDF Conversion");
    }
}