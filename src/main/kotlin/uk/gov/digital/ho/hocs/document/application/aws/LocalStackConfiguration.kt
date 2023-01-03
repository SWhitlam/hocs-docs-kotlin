package uk.gov.digital.ho.hocs.document.application.aws

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.endpoints.DefaultEndpointProvider
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.Url

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("local")
class LocalStackConfiguration(@Value("\${aws.sqs.config.url}") val awsBaseUrl: String) {

    @Primary
    @Bean
    fun sqsClient(@Value("\${aws.region}") awsRegion: String): SqsClient? {

        return runBlocking { SqsClient.fromEnvironment {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(Credentials("test", "test"))
            endpointUrl = Url.parse(awsBaseUrl)
        }
        }
    }

    @Primary
    @Bean
    fun snsClient(@Value("\${aws.region}") awsRegion: String): SnsClient {
        return runBlocking { SnsClient.fromEnvironment {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(Credentials("test", "test"))
            endpointUrl = Url.parse(awsBaseUrl)
            }
        }
    }

    @Bean("Trusted")
    fun trustedS3Client(@Value("\${aws.region}") awsRegion: String): S3Client {
        return runBlocking {
            S3Client.fromEnvironment {
                region = awsRegion
                forcePathStyle = true
                credentialsProvider = StaticCredentialsProvider(Credentials("test", "test"))
                endpointUrl = Url.parse(awsBaseUrl)
            }
        }
    }

    @Bean("UnTrusted")
    fun untrustedS3Client(@Value("\${aws.region}") awsRegion: String): S3Client {
        return runBlocking {
            S3Client.fromEnvironment {
                region = awsRegion
                forcePathStyle = true
                credentialsProvider = StaticCredentialsProvider(Credentials("test", "test"))
                endpointUrl = Url.parse(awsBaseUrl)
            }
        }
    }
}
