package com.menta.api.users.shared.other.config.security

import com.menta.api.users.shared.other.error.ErrorHandler
import com.menta.api.users.shared.other.error.model.ApiErrorResponse
import com.menta.libs.security.SecurityErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityErrorHandler(handler: ErrorHandler): SecurityErrorHandler<ApiErrorResponse> =
        SecurityErrorHandler { handler.handleSecurityException(it) }
}
