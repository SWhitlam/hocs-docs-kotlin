package uk.gov.digital.ho.hocs.document.application.aws

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("s3")
class S3Configuration {

    @Bean("Trusted")
    suspend fun trustedS3Client(@Value("\${aws.s3.untrusted.access.key}") accessKey: String,
                                @Value("\${aws.s3.untrusted.secret.key}") secretKey: String,
                                @Value("\${aws.sqs.region}") awsRegion: String): S3Client {
        return S3Client.fromEnvironment {
            region = awsRegion
            credentialsProvider =  StaticCredentialsProvider(Credentials(accessKey, secretKey))
        }
    }

    @Bean("UnTrusted")
    suspend fun untrustedS3Client(@Value("\${aws.s3.untrusted.access.key}") accessKey: String,
                                @Value("\${aws.s3.untrusted.secret.key}") secretKey: String,
                                @Value("\${aws.sqs.region}") awsRegion: String): S3Client {
        return S3Client.fromEnvironment {
            region = awsRegion
            credentialsProvider =  StaticCredentialsProvider(Credentials(accessKey, secretKey))
        }
    }
}