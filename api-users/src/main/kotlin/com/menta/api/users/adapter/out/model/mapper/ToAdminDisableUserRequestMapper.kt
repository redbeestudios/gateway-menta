package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest
import com.menta.api.users.domain.Email
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminDisableUserRequestMapper {

    fun mapFrom(email: Email, pool: UserPool): AdminDisableUserRequest =
        AdminDisableUserRequest()
            .withUserPoolId(pool.code)
            .withUsername(email)
            .log { info("disable user request mapped: {}", it) }

    companion object : CompanionLogger()
}
