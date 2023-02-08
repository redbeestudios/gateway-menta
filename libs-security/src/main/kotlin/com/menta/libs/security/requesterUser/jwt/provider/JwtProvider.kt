package com.menta.libs.security.requesterUser.jwt.provider

import org.springframework.security.oauth2.jwt.Jwt

fun interface JwtProvider {
    fun provide(): Jwt
}
