package uk.gov.digital.ho.hocs.document.application.aws;

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.sqs.SqsClient;
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.endpoints.Endpoint
import aws.smithy.kotlin.runtime.http.endpoints.EndpointProvider
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("sqs" )
class SqsConfiguration {

    @Primary
    @Bean
    suspend fun sqsClient(@Value("\${aws.sns.audit-search.account.access-key}") accessKey: String,
                          @Value("\${aws.sns.audit-search.account.secret-key}") secretKey: String,
                          @Value("\${aws.sns.config.region}") awsRegion: String): SqsClient {
        return SqsClient.fromEnvironment {
            region = awsRegion
            credentialsProvider =  StaticCredentialsProvider(Credentials(accessKey, secretKey))
        }
    }
}


