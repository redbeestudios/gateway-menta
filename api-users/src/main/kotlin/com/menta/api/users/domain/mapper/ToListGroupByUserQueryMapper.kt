package com.menta.api.users.domain.mapper

import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupByUserQuery.Search
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListGroupByUserQueryMapper {

    fun mapFrom(user: User, next: String?, limit: Int?) =
        ListGroupByUserQuery(
            user = user,
            search = Search(
                limit = limit,
                next = next?.replace(" ", "+")
            )
        ).log { info("query mapped: {}", it) }

    companion object : CompanionLogger()
}
