package com.menta.libs.security.authenticationManager.provider

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider

class GenericAuthenticationManagerProvider : AuthenticationManagerProvider {
    override fun provideFor(
        issuer: Issuer,
        jwtAuthenticationConverter: JwtAuthenticationConverter
    ): AuthenticationManager =
        with(issuer) {
            JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(uri))
                .apply { setJwtAuthenticationConverter(jwtAuthenticationConverter) }
                .asAuthenticationManager()
        }

    private fun JwtAuthenticationProvider.asAuthenticationManager() =
        AuthenticationManager { authenticate(it) }
}
