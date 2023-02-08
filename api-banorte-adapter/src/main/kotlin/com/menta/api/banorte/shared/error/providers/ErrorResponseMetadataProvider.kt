package com.menta.api.banorte.shared.error.providers

import com.menta.api.banorte.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ErrorResponseMetadataProvider(
    private val currentResourceProvider: CurrentResourceProvider,
) {

    fun provide() =
        mapOf(
            QUERY_STRING to currentResourceProvider.provideQuery()
        )
            .log { debug("metadata provided: {}", it) }

    companion object : CompanionLogger() {
        private const val QUERY_STRING = "query_string"
    }
}
