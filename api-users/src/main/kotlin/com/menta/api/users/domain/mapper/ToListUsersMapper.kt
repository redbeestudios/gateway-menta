package com.menta.api.users.domain.mapper

import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListUsersMapper {

    fun mapFrom(query: ListUsersFilterByQuery, result: ListUsersFilterByQueryResult) =
        ListUserPage(
            users = result.users,
            next = result.next,
            limit = query.search.limit
        ).log { info("users mapped from query and result: {}", it) }

    companion object : CompanionLogger()
}
