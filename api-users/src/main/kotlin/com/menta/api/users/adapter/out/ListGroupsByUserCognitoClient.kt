package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult
import com.menta.api.users.adapter.`in`.model.mapper.ToAdminListGroupForUserRequestMapper
import com.menta.api.users.application.port.out.ListGroupsByUserPortOut
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.domain.mapper.ToListGroupsByUserQueryResultMapper
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class ListGroupsByUserCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminListGroupForUserRequestMapper: ToAdminListGroupForUserRequestMapper,
    private val toListGroupsByUserQueryResultMapper: ToListGroupsByUserQueryResultMapper,
    private val userPoolProvider: UserPoolProvider
) : ListGroupsByUserPortOut {

    override fun list(query: ListGroupByUserQuery): Either<ApplicationError, ListGroupsByUserQueryResult> =
        log.benchmark("list groups by user: $query") {
            try {
                query.toRequest().map {
                    it.doList()
                        .toQueryResult()
                }
            } catch (e: Throwable) {
                e.handleCognitoError(query.user.attributes.email).left()
            }
        }

    private fun ListGroupByUserQuery.toRequest() =
        userPoolProvider.provideFor(user.attributes.type).map {
            toAdminListGroupForUserRequestMapper.mapFrom(this, it)
        }.logRight { info("request mapped: {}", it) }

    private fun AdminListGroupsForUserRequest.doList() =
        awsCognitoIdentityProvider.adminListGroupsForUser(this)
            .log { info("list: {}", it) }

    private fun AdminListGroupsForUserResult.toQueryResult() =
        toListGroupsByUserQueryResultMapper.mapFrom(this)
            .log { info("query result mapped: {}", it) }

    companion object : CompanionLogger()
}
