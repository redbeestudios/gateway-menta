package com.menta.libs.security.requesterUser.jwt.provider

import com.menta.libs.security.requesterUser.jwt.exception.MissingJWTException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class FromSecurityContextJwtProvider : JwtProvider {

    override fun provide(): Jwt =
        SecurityContextHolder
            .getContext()
            ?.authentication
            ?.takeIf { it is JwtAuthenticationToken }
            ?.let { it.principal as Jwt? }
            ?: throw MissingJWTException()
}
