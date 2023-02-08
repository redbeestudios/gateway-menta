package com.menta.bff.devices.login.login.auth.adapter.out.model.mapper

import com.menta.bff.devices.login.login.auth.adapter.out.model.LoginClientRequest
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToLoginClientRequestMapper {

    fun mapFrom(credentials: UserCredentials): LoginClientRequest =
        with(credentials) {
            LoginClientRequest(
                user = user,
                password = password,
                userType = userType
            )
        }.log { info("login client request mapped: {}", it) }

    companion object : CompanionLogger()
}
