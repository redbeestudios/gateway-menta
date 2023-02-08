package com.kiwi.api.reverse.shared.error.providers

import com.kiwi.api.reverse.config.TraceSleuthInterceptor.Companion.SPAN_ID_DOESNT_EXIST
import com.kiwi.api.reverse.config.TraceSleuthInterceptor.Companion.TRACE_ID_DOESNT_EXIST
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.cloud.sleuth.Tracer
import org.springframework.stereotype.Component

@Component
class TracingProvider(
    private val tracer: Tracer
) {

    fun provide() =
        Tracing(
            traceId = tracer.currentSpan()?.context()?.traceId() ?: TRACE_ID_DOESNT_EXIST,
            spanId = tracer.currentSpan()?.context()?.spanId() ?: SPAN_ID_DOESNT_EXIST
        ).log { debug("trace provided: {}", it) }

    companion object : CompanionLogger()

}

data class Tracing(
    val traceId: String,
    val spanId: String
)
