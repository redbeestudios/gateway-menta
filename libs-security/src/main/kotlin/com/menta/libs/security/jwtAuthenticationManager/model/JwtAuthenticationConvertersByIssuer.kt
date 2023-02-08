package com.menta.libs.security.jwtAuthenticationManager.model

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

typealias JwtAuthenticationConverterByIssuer = Map<Issuer, JwtAuthenticationConverter>

fun JwtAuthenticationConverterByIssuer.getOrThrow(item: Issuer): JwtAuthenticationConverter =
    this[item] ?: throw java.lang.RuntimeException()
