package com.menta.bff.devices.login.login.revoke.adapter.out.model.mapper

import com.menta.bff.devices.login.login.revoke.adapter.out.model.RevokeTokenClientRequest
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToRevokeTokenClientRequestMapper {

    fun mapFrom(revokeToken: RevokeToken): RevokeTokenClientRequest =
        with(revokeToken) {
            RevokeTokenClientRequest(
                refreshToken = token,
                userType = userType
            )
        }.log { info("revoke token client request mapped: {}", it) }

    companion object : CompanionLogger()
}
