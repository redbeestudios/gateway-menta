package com.menta.api.users.domain.mapper

import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserWithGroupsMapper {

    fun mapFrom(query: ListGroupByUserQuery, result: ListGroupsByUserQueryResult) =
        UserWithGroups(
            user = query.user,
            groups = result.groups,
            next = result.next,
            limit = query.search.limit
        ).log { info("user with group mapped from query and result: {}", it) }

    companion object : CompanionLogger()
}
