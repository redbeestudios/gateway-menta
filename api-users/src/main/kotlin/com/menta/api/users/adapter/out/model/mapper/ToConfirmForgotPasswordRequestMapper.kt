package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToConfirmForgotPasswordRequestMapper {
    fun mapFrom(
        confirmForgotPasswordUser: ConfirmForgotPasswordUser,
        userPool: UserPool
    ): ConfirmForgotPasswordRequest =
        with(confirmForgotPasswordUser) {
            ConfirmForgotPasswordRequest()
                .withUsername(email)
                .withClientId(userPool.clientId)
                .withConfirmationCode(code)
                .withPassword(password)
        }.log { info("confirm forgot password user request mapped: {}", it) }

    companion object : CompanionLogger()
}
