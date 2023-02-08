package com.menta.libs.security.jwtAuthenticationManager

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.jwtAuthenticationManager.model.JwtAuthenticationConverterByIssuer
import com.menta.libs.security.jwtAuthenticationManager.provider.JwtAuthenticationConverterProvider
import com.menta.libs.security.jwtAuthenticationManager.provider.SpecificAuthoritiesJwtAuthenticationConverterProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("libsSecurityJwtAuthenticationConverterConfiguration")
class JwtAuthenticationConverterConfiguration(
    private val properties: SecurityConfigurationProperties
) {

    @Bean("libsSecurityJwtAuthenticationConverters")
    fun jwtAuthenticationConverters(
        provider: JwtAuthenticationConverterProvider
    ): JwtAuthenticationConverterByIssuer =
        properties.resourceServer!!.issuers!!.associateWith { provider.provideFor(it) }

    @Bean("libsSecurityJwtAuthenticationConverterProvider")
    fun jwtAuthenticationConverterProvider(): JwtAuthenticationConverterProvider =
        SpecificAuthoritiesJwtAuthenticationConverterProvider()
}
