package com.menta.api.login.login.domain.mapper

import UserCredentials
import com.menta.api.login.login.adapter.`in`.model.LoginRequest
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserCredentialsMapper {

    fun mapFrom(request: LoginRequest) =
        with(request) {
            UserCredentials(
                user = user,
                password = password,
                userType = userType
            )
        }.log { info("user credentials mapped: {}", it) }

    companion object : CompanionLogger()
}
