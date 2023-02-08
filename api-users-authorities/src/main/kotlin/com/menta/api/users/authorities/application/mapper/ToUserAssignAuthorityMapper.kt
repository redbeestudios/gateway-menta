package com.menta.api.users.authorities.application.mapper

import com.menta.api.users.authorities.adapter.`in`.consumer.model.User
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserAssignAuthorityMapper {

    fun mapFrom(user: User, authority: String) =
        with(user) {
            UserAssignAuthority(
                user = attributes.email,
                type = attributes.type,
                authority = authority
            )
        }.log { info("user assign authority mapped: {}", it) }

    companion object : CompanionLogger()
}
