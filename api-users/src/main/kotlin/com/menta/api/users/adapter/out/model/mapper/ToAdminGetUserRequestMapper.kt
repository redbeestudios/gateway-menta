package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest
import com.menta.api.users.domain.Email
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminGetUserRequestMapper {

    fun mapFrom(email: Email, pool: UserPool): AdminGetUserRequest =
        AdminGetUserRequest()
            .withUserPoolId(pool.code)
            .withUsername(email)
            .log { info("get user request mapped: {}", it) }

    companion object : CompanionLogger()
}
