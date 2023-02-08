package com.menta.api.users.adapter.`in`.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminListGroupForUserRequestMapper {

    fun mapFrom(query: ListGroupByUserQuery, pool: UserPool): AdminListGroupsForUserRequest =
        with(query) {
            AdminListGroupsForUserRequest()
                .withUserPoolId(pool.code)
                .withUsername(user.attributes.email)
                .apply { search.limit?.let { withLimit(it) } }
                .apply { search.next?.let { withNextToken(it) } }
        }.log { info("request mapped from query and pool: {}", it) }

    companion object : CompanionLogger()
}
