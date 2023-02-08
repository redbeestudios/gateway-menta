package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.SetUserPasswordRequest
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToSetUserPasswordMapper {

    fun mapFrom(request: SetUserPasswordRequest): SetUserPassword =
        with(request) {
            SetUserPassword(
                type = userType,
                email = email,
                password = password,
                permanent = permanent
            )
        }.log { info("set user password mapped: {}", it) }

    companion object : CompanionLogger()
}
