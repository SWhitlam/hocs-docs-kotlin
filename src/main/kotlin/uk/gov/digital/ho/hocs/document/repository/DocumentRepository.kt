package uk.gov.digital.ho.hocs.document.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import uk.gov.digital.ho.hocs.document.model.DocumentData
import java.util.UUID

interface DocumentRepository: CoroutineCrudRepository<DocumentData, Long> {

    @Query(value = "SELECT * FROM document_data WHERE uuid = ?1")
    suspend fun findByUuid(uuid: UUID): DocumentData?

    @Query(value = "SELECT * FROM document_data WHERE uuid = ?1 AND NOT deleted")
    suspend fun findActiveByUuid(uuid: UUID): DocumentData?

    @Query(value = "SELECT * FROM document_data WHERE external_reference_uuid = ?1 AND NOT deleted")
    suspend fun findAllActiveByExternalReferenceUUID(externalReferenceUUID: UUID): Flow<DocumentData>

}