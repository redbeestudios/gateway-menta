package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest
import com.menta.api.users.adapter.out.model.mapper.ToConfirmForgotPasswordRequestMapper
import com.menta.api.users.application.port.out.ConfirmPasswordForgotUserPortOut
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ConfirmForgotPasswordCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toConfirmForgotPasswordRequestMapper: ToConfirmForgotPasswordRequestMapper,
    private val userPoolProvider: UserPoolProvider
) : ConfirmPasswordForgotUserPortOut {

    override fun confirm(confirmForgotPasswordUser: ConfirmForgotPasswordUser): Either<ApplicationError, Unit> =
        try {
            buildRequest(confirmForgotPasswordUser).flatMap {
                it.confirmForgotPassword().let { Unit.right() }
            }
        } catch (e: Throwable) {
            e.handleCognitoError(confirmForgotPasswordUser.email).left()
        }

    private fun buildRequest(confirmForgotPasswordUser: ConfirmForgotPasswordUser) =
        userPoolProvider.provideFor(confirmForgotPasswordUser.type).map {
            toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgotPasswordUser, it)
        }.log { info("request builded: {}", it) }

    private fun ConfirmForgotPasswordRequest.confirmForgotPassword() =
        awsCognitoIdentityProvider.confirmForgotPassword(this)
            .log { info("confirm forgot password: {}", it) }

    companion object : CompanionLogger()
}
