package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest
import com.menta.api.users.adapter.out.model.mapper.ToForgotPasswordRequestMapper
import com.menta.api.users.application.port.out.ForgotPasswordUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ForgotPasswordCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toForgotPasswordRequestMapper: ToForgotPasswordRequestMapper,
    private val userPoolProvider: UserPoolProvider
) : ForgotPasswordUserPortOut {

    override fun retrieve(email: Email, type: UserType): Either<ApplicationError, Unit> =
        try {
            buildRequest(email, type).flatMap {
                it.forgotPassword().let { Unit.right() }
            }
        } catch (e: Throwable) {
            e.handleCognitoError(email).left()
        }

    private fun buildRequest(email: Email, type: UserType) =
        userPoolProvider.provideFor(type).map {
            toForgotPasswordRequestMapper.mapFrom(email, it)
        }.log { info("request builded: {}", it) }

    private fun ForgotPasswordRequest.forgotPassword() =
        awsCognitoIdentityProvider.forgotPassword(this)
            .log { info("forgot password: {}", it) }

    companion object : CompanionLogger()
}
