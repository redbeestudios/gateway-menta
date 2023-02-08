package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.adapter.`in`.model.ListUsersResponse
import com.menta.api.users.adapter.`in`.model.ListUsersResponse.SearchMetadata
import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListUsersResponseMapper(
    private val toUserResponseMapper: ToUserResponseMapper
) {

    fun mapFrom(listUsersPage: ListUserPage) =
        with(listUsersPage) {
            ListUsersResponse(
                users = users.map { it.map() },
                _metadata = SearchMetadata(
                    _next = next,
                    _limit = limit
                )
            )
        }.log { info("response mapped: {}", it) }

    private fun User.map() =
        toUserResponseMapper.mapFrom(this)
            .log { info("user response mapped: {}", it) }

    companion object : CompanionLogger()
}
