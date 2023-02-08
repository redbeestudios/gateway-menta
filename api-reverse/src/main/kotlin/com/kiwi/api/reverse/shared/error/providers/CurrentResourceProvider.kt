package com.kiwi.api.reverse.shared.error.providers

import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class CurrentResourceProvider(
    private val httpServletRequest: HttpServletRequest
) {
    fun provideUri() =
        httpServletRequest.requestURI
            .log { debug("uri provided: {}", it) }

    fun provideQuery() =
        (httpServletRequest.queryString ?: "")
            .log { debug("query string provided: {}", it) }


    companion object : CompanionLogger()
}
