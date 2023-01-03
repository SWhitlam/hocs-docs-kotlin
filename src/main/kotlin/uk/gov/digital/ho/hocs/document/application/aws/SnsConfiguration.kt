package uk.gov.digital.ho.hocs.document.application.aws

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.sns.SnsClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("sns")
class SnsConfiguration {

    @Primary
    @Bean
    fun snsClient(@Value("\${aws.sns.audit-search.account.access-key}") accessKey: String,
                           @Value("\${aws.sns.audit-search.account.secret-key}") secretKey: String,
                           @Value("\${aws.sns.config.region}") awsRegion: String): SnsClient {
        return runBlocking {
        SnsClient.fromEnvironment {
            region = awsRegion
            credentialsProvider =  StaticCredentialsProvider(Credentials(accessKey, secretKey))
        }
        }
    }
}