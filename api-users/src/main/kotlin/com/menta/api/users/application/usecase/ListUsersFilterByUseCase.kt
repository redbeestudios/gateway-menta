package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.ListUsersFilterByPortIn
import com.menta.api.users.application.port.out.ListUsersFilterByPortOut
import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.mapper.ToListUsersMapper
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ListUsersFilterByUseCase(
    private val portOut: ListUsersFilterByPortOut,
    private val toListUsersMapper: ToListUsersMapper
) : ListUsersFilterByPortIn {

    override fun list(query: ListUsersFilterByQuery): Either<ApplicationError, ListUserPage> =
        query.doList()
            .map { buildListUser(query, it) }

    private fun ListUsersFilterByQuery.doList() =
        portOut.list(this)
            .logRight { info("users found: {}", it) }

    private fun buildListUser(query: ListUsersFilterByQuery, result: ListUsersFilterByQueryResult) =
        toListUsersMapper.mapFrom(query, result)
            .log { info("users page mapped: {}", it) }

    companion object : CompanionLogger()
}
