package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest
import com.menta.api.users.domain.Email
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToForgotPasswordRequestMapper {
    fun mapFrom(email: Email, userPool: UserPool): ForgotPasswordRequest =
        ForgotPasswordRequest()
            .withUsername(email)
            .withClientId(userPool.clientId)
            .log { info("forgot password user request mapped: {}", it) }

    companion object : CompanionLogger()
}
