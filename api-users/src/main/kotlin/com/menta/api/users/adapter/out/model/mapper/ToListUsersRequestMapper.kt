package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.ListUsersRequest
import com.menta.api.users.adapter.out.model.CognitoFieldName
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListUsersRequestMapper {

    fun mapFrom(query: ListUsersFilterByQuery, pool: UserPool): ListUsersRequest =
        with(query) {
            ListUsersRequest()
                .withUserPoolId(pool.code)
                .withFilter(query.getCustomFilter())
                .apply { search.limit?.let { withLimit(it) } }
                .apply { search.next?.let { withPaginationToken(it) } }
        }.log { info("request mapped from query and pool: {}", it) }

    fun ListUsersFilterByQuery.getCustomFilter() =
        this.email?.let { CognitoFieldName.EMAIL.fieldName.plus(" = \"$it\"") }
            ?: this.customerId?.let { CognitoFieldName.CUSTOMER_ID.fieldName.plus(" = \"$it\"") }
            ?: this.merchantId?.let { CognitoFieldName.MERCHANT_ID.fieldName.plus(" = \"$it\"") }

    companion object : CompanionLogger()
}
