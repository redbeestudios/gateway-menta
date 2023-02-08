package com.menta.libs.security.jwtAuthenticationManager.provider

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

class SpecificAuthoritiesJwtAuthenticationConverterProvider : JwtAuthenticationConverterProvider {
    override fun provideFor(issuer: Issuer): JwtAuthenticationConverter =
        JwtAuthenticationConverter()
            .apply {
                setJwtGrantedAuthoritiesConverter {
                    it.extractAuthorities(issuer.authoritiesClaimKey)
                }
            }

    private fun Jwt.extractAuthorities(authorityKey: String): MutableCollection<GrantedAuthority> =
        (
            (claims[authorityKey] as Collection<String>?)
                ?.map { SimpleGrantedAuthority(it) }
                ?: emptyList<GrantedAuthority>()
            )
            .toMutableList()
}
