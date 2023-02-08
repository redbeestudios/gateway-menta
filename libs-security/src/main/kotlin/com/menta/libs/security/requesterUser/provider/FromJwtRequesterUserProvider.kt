package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.requesterUser.jwt.provider.JwtProvider
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.mapper.ToRequesterUserMapper
import org.springframework.security.oauth2.jwt.Jwt

class FromJwtRequesterUserProvider(
    private val jwtProvider: JwtProvider,
    private val toRequesterUserMapper: ToRequesterUserMapper
) : RequesterUserProvider {

    override fun provide(): RequesterUser =
        jwt().toRequesterUser()

    private fun jwt() =
        jwtProvider.provide()

    private fun Jwt.toRequesterUser() =
        toRequesterUserMapper.from(this)
}
