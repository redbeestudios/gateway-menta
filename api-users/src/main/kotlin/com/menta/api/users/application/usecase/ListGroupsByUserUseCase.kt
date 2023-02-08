package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.ListGroupsByUserPortIn
import com.menta.api.users.application.port.out.ListGroupsByUserPortOut
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.domain.mapper.ToUserWithGroupsMapper
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ListGroupsByUserUseCase(
    private val portOut: ListGroupsByUserPortOut,
    private val toUserWithGroupsMapper: ToUserWithGroupsMapper
) : ListGroupsByUserPortIn {

    override fun list(query: ListGroupByUserQuery): Either<ApplicationError, UserWithGroups> =
        query.doList()
            .map { buildUserWithGroups(query, it) }

    private fun ListGroupByUserQuery.doList() =
        portOut.list(this)
            .logRight { info("groups found: {}", it) }

    private fun buildUserWithGroups(query: ListGroupByUserQuery, result: ListGroupsByUserQueryResult) =
        toUserWithGroupsMapper.mapFrom(query, result)
            .log { info("user with groups mapped: {}", it) }

    companion object : CompanionLogger()
}
