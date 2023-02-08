package com.menta.api.login.shared.other.error.providers

import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class CurrentResourceProvider(
    private val httpServletRequest: HttpServletRequest
) {
    fun provideUri(): String =
        httpServletRequest.requestURI
            .log { debug("uri provided: {}", it) }

    fun provideQuery(): String =
        (httpServletRequest.queryString ?: "")
            .log { debug("query string provided: {}", it) }

    companion object : CompanionLogger()
}
