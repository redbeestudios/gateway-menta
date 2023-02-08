package com.menta.libs.security.jwtIssuerAuthenticationManagerResolver.provider

import com.menta.libs.security.authenticationManager.model.AuthenticationManagerByIssuer
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver

fun interface JwtIssuerAuthenticationManagerResolverProvider {

    fun provideFor(
        authenticationManagerByIssuer: AuthenticationManagerByIssuer
    ): JwtIssuerAuthenticationManagerResolver
}
