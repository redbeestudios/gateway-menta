package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminSetUserPasswordRequestMapper {
    fun mapFrom(setUserPassword: SetUserPassword, userPool: UserPool): AdminSetUserPasswordRequest =
        with(setUserPassword) {
            AdminSetUserPasswordRequest()
                .withUsername(email)
                .withUserPoolId(userPool.code)
                .withPassword(password)
                .withPermanent(permanent)
        }.log { info("set password user request mapped: {}", it) }

    companion object : CompanionLogger()
}
