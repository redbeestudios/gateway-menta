package com.menta.libs.security.authenticationManager.provider

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

fun interface AuthenticationManagerProvider {
    fun provideFor(
        issuer: Issuer,
        jwtAuthenticationConverter: JwtAuthenticationConverter
    ): AuthenticationManager
}
