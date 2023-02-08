package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.domain.NewUser
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToNewUserMapper {

    fun mapFrom(request: CreateUserRequest): NewUser =
        with(request) {
            NewUser(
                type = userType,
                attributes = NewUser.Attributes(
                    email = attributes.email,
                    customerId = attributes.customerId,
                    merchantId = attributes.merchantId
                )
            )
        }.log { info("new user mapped: {}", it) }

    companion object : CompanionLogger()
}
