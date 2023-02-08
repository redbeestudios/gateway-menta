package com.menta.libs.security.jwtIssuerAuthenticationManagerResolver

import com.menta.libs.security.authenticationManager.model.AuthenticationManagerByIssuer
import com.menta.libs.security.jwtIssuerAuthenticationManagerResolver.provider.GenericJwtIssuerAuthenticationManagerResolverProvider
import com.menta.libs.security.jwtIssuerAuthenticationManagerResolver.provider.JwtIssuerAuthenticationManagerResolverProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver

@Configuration("libsSecurityJwtIssuerAuthenticationManagerResolverConfiguration")
class JwtIssuerAuthenticationManagerResolverConfiguration {

    @Bean("libsSecurityJwtIssuerAuthenticationManagerResolver")
    fun jwtIssuerAuthenticationManagerResolver(
        provider: JwtIssuerAuthenticationManagerResolverProvider,
        authenticationManagerByIssuer: AuthenticationManagerByIssuer
    ): JwtIssuerAuthenticationManagerResolver =
        provider.provideFor(authenticationManagerByIssuer)

    @Bean("libsSecurityJwtIssuerAuthenticationManagerResolverProvider")
    fun jwtIssuerAuthenticationManagerResolverProvider(): JwtIssuerAuthenticationManagerResolverProvider =
        GenericJwtIssuerAuthenticationManagerResolverProvider()
}
