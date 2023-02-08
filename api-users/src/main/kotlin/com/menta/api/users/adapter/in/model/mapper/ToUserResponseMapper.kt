package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserResponseMapper {

    fun mapFrom(user: User): UserResponse =
        with(user) {
            UserResponse(
                attributes = with(attributes) {
                    UserResponse.Attributes(
                        email = email,
                        merchantId = merchantId,
                        customerId = customerId,
                        type = type
                    )
                },
                status = status,
                audit = UserResponse.Audit(
                    creationDate = audit.creationDate,
                    updateDate = audit.updateDate
                )
            )
        }.log { info("user response mapped: {}", it) }

    companion object : CompanionLogger()
}
