package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest
import com.menta.api.users.adapter.out.model.mapper.ToAdminSetUserPasswordRequestMapper
import com.menta.api.users.application.port.out.SetUserPasswordPortOut
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class SetUserPasswordCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminSetUserPasswordRequestMapper: ToAdminSetUserPasswordRequestMapper,
    private val userPoolProvider: UserPoolProvider
) : SetUserPasswordPortOut {

    override fun setPassword(setUserPassword: SetUserPassword): Either<ApplicationError, Unit> =
        try {
            setUserPassword.toRequest().flatMap {
                it.doSetPassword().let { Unit.right() }
            }
        } catch (e: Throwable) {
            e.handleCognitoError(setUserPassword.email).left()
        }

    private fun SetUserPassword.toRequest() =
        userPoolProvider.provideFor(type).map {
            toAdminSetUserPasswordRequestMapper.mapFrom(this, it)
        }.logRight { info("request mapped: {}", it) }

    private fun AdminSetUserPasswordRequest.doSetPassword() =
        awsCognitoIdentityProvider.adminSetUserPassword(this)
            .log { info("user set password: {}", it) }

    companion object : CompanionLogger()
}
