package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.ListUsersResult
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListUsersFilterByQueryResultMapper(
    private val toUserMapper: ToUserMapper
) {
    fun mapFrom(result: ListUsersResult, type: UserType) =
        with(result) {
            ListUsersFilterByQueryResult(
                users = result.users.map { toUserMapper.mapFrom(it, type) },
                next = paginationToken
            )
        }.log { info("query result mapped from cognito result: {}", it) }

    companion object : CompanionLogger()
}
