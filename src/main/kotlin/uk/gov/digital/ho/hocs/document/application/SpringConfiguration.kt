package uk.gov.digital.ho.hocs.document.application

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import java.text.SimpleDateFormat
import java.time.Clock


@Configuration
@EnableR2dbcRepositories
class SpringConfiguration(
    @Value("\${hocs.converter-service}") private val converterService: String,
    @Value("\${hocs.clamav-service}") private val clamavService: String) {

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Bean("converter")
    fun createConverterWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(converterService)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
            .build()
    }

    @Bean("clamav")
    fun createClamAVWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(clamavService)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
            .build()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
                .registerKotlinModule()
                .setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModules(JavaTimeModule())
    }


}