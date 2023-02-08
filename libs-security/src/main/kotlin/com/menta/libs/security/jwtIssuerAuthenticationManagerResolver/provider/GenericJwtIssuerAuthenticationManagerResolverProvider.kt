package com.menta.libs.security.jwtIssuerAuthenticationManagerResolver.provider

import com.menta.libs.security.authenticationManager.model.AuthenticationManagerByIssuer
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver

class GenericJwtIssuerAuthenticationManagerResolverProvider :
    JwtIssuerAuthenticationManagerResolverProvider {

    override fun provideFor(
        authenticationManagerByIssuer: AuthenticationManagerByIssuer
    ): JwtIssuerAuthenticationManagerResolver =
        JwtIssuerAuthenticationManagerResolver(
            authenticationManagerByIssuer
                .mapKeys { it.key.uri }::get
        )
}
