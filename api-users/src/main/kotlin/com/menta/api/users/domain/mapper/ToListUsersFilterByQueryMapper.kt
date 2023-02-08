package com.menta.api.users.domain.mapper

import com.menta.api.users.domain.Email
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToListUsersFilterByQueryMapper {

    fun mapFrom(type: UserType, email: Email?, customerId: UUID?, merchantId: UUID?, limit: Int?, next: String?) =
        ListUsersFilterByQuery(
            type = type,
            email = email,
            customerId = customerId,
            merchantId = merchantId,
            search = ListUsersFilterByQuery.Search(
                limit = limit,
                next = next?.replace(" ", "+")
            )
        ).log { info("query mapped: {}", it) }

    companion object : CompanionLogger()
}
