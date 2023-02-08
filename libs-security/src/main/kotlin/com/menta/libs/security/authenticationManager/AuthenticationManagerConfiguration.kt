package com.menta.libs.security.authenticationManager

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.authenticationManager.model.AuthenticationManagerByIssuer
import com.menta.libs.security.authenticationManager.provider.AuthenticationManagerProvider
import com.menta.libs.security.authenticationManager.provider.GenericAuthenticationManagerProvider
import com.menta.libs.security.jwtAuthenticationManager.model.JwtAuthenticationConverterByIssuer
import com.menta.libs.security.jwtAuthenticationManager.model.getOrThrow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("libsSecurityAuthenticationManagerConfiguration")
class AuthenticationManagerConfiguration(
    private val properties: SecurityConfigurationProperties
) {

    @Bean("libsSecurityAuthenticationManagers")
    fun authenticationManagers(
        provider: AuthenticationManagerProvider,
        convertersByIssuer: JwtAuthenticationConverterByIssuer
    ): AuthenticationManagerByIssuer =
        properties.resourceServer!!.issuers!!.associateWith {
            provider.provideFor(it, convertersByIssuer.getOrThrow(it))
        }

    @Bean("libsSecurityAuthenticationManagerProvider")
    fun authenticationManagerProvider(): AuthenticationManagerProvider =
        GenericAuthenticationManagerProvider()
}
