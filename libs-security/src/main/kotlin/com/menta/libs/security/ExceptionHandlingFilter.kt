package com.menta.libs.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ExceptionHandlingFilter(
    private val objectMapper: ObjectMapper,
    private val handler: SecurityErrorHandler<*>
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: RuntimeException) {
            handler.handle(e).let {
                response.status = it.statusCodeValue
                response.writer.write(objectMapper.writeValueAsString(it.body))
                response.contentType = "application/json"
            }
        }
    }

}

fun interface SecurityErrorHandler<T> {
    fun handle(e: Exception): ResponseEntity<T>
}