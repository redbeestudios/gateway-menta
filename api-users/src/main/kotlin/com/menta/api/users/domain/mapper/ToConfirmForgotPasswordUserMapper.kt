package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.ConfirmForgotPasswordUserRequest
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToConfirmForgotPasswordUserMapper {

    fun mapFrom(request: ConfirmForgotPasswordUserRequest, email: Email, type: UserType): ConfirmForgotPasswordUser =
        with(request) {
            ConfirmForgotPasswordUser(
                type = type,
                email = email,
                password = password,
                code = code
            )
        }.log { info("confirm forgot password user mapped: {}", it) }

    companion object : CompanionLogger()
}
