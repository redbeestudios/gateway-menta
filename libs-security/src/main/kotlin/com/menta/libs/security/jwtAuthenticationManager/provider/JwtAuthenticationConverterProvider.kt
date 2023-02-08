package com.menta.libs.security.jwtAuthenticationManager.provider

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

fun interface JwtAuthenticationConverterProvider {
    fun provideFor(issuer: Issuer): JwtAuthenticationConverter
}
