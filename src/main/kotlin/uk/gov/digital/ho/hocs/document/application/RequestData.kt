package uk.gov.digital.ho.hocs.document.application

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class RequestDataFilter : WebFilter {

    override fun filter(serverWebExchange: ServerWebExchange,
                        webFilterChain: WebFilterChain): Mono<Void> {
        val headers = serverWebExchange.request.headers

        val correlationid: String = headers[CORRELATION_ID_HEADER]?.firstOrNull() ?: UUID.randomUUID().toString()
        val groups: String = headers[GROUP_HEADER]?.firstOrNull() ?: "/QU5PTllNT1VTCg=="
        val userid: String = headers[USER_ID_HEADER]?.firstOrNull() ?: ANONYMOUS
        val username: String = headers[USERNAME_HEADER]?.firstOrNull() ?: ANONYMOUS

        return webFilterChain
            .filter(serverWebExchange)
            .contextWrite {
                it.putAllMap(mapOf(
                    CORRELATION_ID_HEADER to correlationid,
                    GROUP_HEADER to groups,
                    USER_ID_HEADER to userid,
                    USERNAME_HEADER to username
                ))
            }
    }

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val USER_ID_HEADER = "X-Auth-UserId"
        const val USERNAME_HEADER = "X-Auth-Username"
        const val GROUP_HEADER = "X-Auth-Groups"
        const val ANONYMOUS = "anonymous"
    }


}