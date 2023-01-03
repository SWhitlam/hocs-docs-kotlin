package uk.gov.digital.ho.hocs.document.service

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3Service(
    private val objectMapper: ObjectMapper,
    @Value("\${aws.s3.untrusted.bucket-name}") private val untrustedS3BucketName: String,
    @Value("\${aws.s3.trusted.bucket-name}") private val trustedS3BucketName: String,
    @Qualifier("UnTrusted") private val untrustedS3Client: S3Client,
    @Qualifier("Trusted") private val trustedS3Client: S3Client,
    ) {


    suspend fun getUnTrustedObject(objectKey : String): ByteArray {
        return untrustedS3Client.getObject(GetObjectRequest {
            bucket = untrustedS3BucketName
            key = objectKey
        }) {
            it.body?.toByteArray() ?: ByteArray(0)
        }
    }

    suspend fun putTrustedObject(objectKey: String, data: ByteArray) {
        trustedS3Client.putObject( PutObjectRequest{
            bucket = trustedS3BucketName
            key = objectKey
            body = ByteStream.fromBytes(data)
        })
    }
}