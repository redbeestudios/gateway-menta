package com.kiwi.api.reverse.config

import com.kiwi.api.reverse.shared.error.providers.TracingProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.AsyncHandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TraceSleuthInterceptor(
    private val tracingProvider: TracingProvider
) : AsyncHandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ) =
        tracingProvider.provide()
            .also {
                response.addHeaderIfDoesntExist(X_B3_TRACE_ID, it.traceId)
                response.addHeaderIfDoesntExist(X_B3_SPAN_ID, it.spanId)
            }.let { true }


    private fun HttpServletResponse.addHeaderIfDoesntExist(headerName: String, value: String) {
        if (!headerName.contains(headerName)) {
            addHeader(headerName, value)
        }
    }

    companion object {
        const val TRACE_ID_DOESNT_EXIST = "Trace id doesnt exist"
        const val SPAN_ID_DOESNT_EXIST = "Span id doesnt exist"
        private const val X_B3_TRACE_ID = "X-B3-TraceId"
        private const val X_B3_SPAN_ID = "X-B3-SpanId"
    }
}
