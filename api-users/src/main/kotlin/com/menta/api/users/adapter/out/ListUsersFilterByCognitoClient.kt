package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.ListUsersRequest
import com.amazonaws.services.cognitoidp.model.ListUsersResult
import com.menta.api.users.adapter.out.model.mapper.ToListUsersRequestMapper
import com.menta.api.users.application.port.out.ListUsersFilterByPortOut
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.mapper.ToListUsersFilterByQueryResultMapper
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class ListUsersFilterByCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toListUsersRequestMapper: ToListUsersRequestMapper,
    private val toListUsersFilterByQueryResultMapper: ToListUsersFilterByQueryResultMapper,
    private val userPoolProvider: UserPoolProvider
) : ListUsersFilterByPortOut {

    override fun list(query: ListUsersFilterByQuery): Either<ApplicationError, ListUsersFilterByQueryResult> =
        log.benchmark("list users by : $query") {
            try {
                query.toRequest().map {
                    it.doList()
                        .toQueryResult(query.type)
                }
            } catch (e: Throwable) {
                e.handleCognitoError(query.email.toString()).left()
            }
        }

    private fun ListUsersFilterByQuery.toRequest() =
        userPoolProvider.provideFor(type).map {
            toListUsersRequestMapper.mapFrom(this, it)
        }.logRight { info("request mapped: {}", it) }

    private fun ListUsersRequest.doList() =
        awsCognitoIdentityProvider.listUsers(this)
            .log { info("list: {}", it) }

    private fun ListUsersResult.toQueryResult(type: UserType) =
        toListUsersFilterByQueryResultMapper.mapFrom(this, type)
            .log { info("query result mapped: {}", it) }

    companion object : CompanionLogger()
}
