package com.menta.api.seeds.shared.config.security

import com.menta.api.seeds.shared.other.error.ErrorHandler
import com.menta.api.seeds.shared.other.error.model.ApiErrorResponse
import com.menta.libs.security.SecurityErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityErrorHandler(handler: ErrorHandler): SecurityErrorHandler<ApiErrorResponse> =
        SecurityErrorHandler { handler.handleSecurityException(it) }
}
