package uk.gov.digital.ho.hocs.document.application

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpClient
import java.util.concurrent.TimeUnit

class WebClientConfiguration {


//    private fun webClientFactory(
//        baseUrl: String,
//        bufferByteCount: Int
//    ): WebClient {
//        val httpClient = HttpClient.create()
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
//            .doOnConnected {
//                it.addHandlerLast(ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
//                    .addHandlerLast(WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS))
//            }
//
//        return WebClient
//            .builder()
//            .clientConnector(ReactorClientHttpConnector(httpClient))
//            .codecs { it.defaultCodecs().maxInMemorySize(bufferByteCount) }
//            .baseUrl(baseUrl)
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .build()
//    }

}