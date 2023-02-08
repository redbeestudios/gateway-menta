package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest
import com.menta.api.users.adapter.out.model.mapper.ToAdminDisableUserRequestMapper
import com.menta.api.users.application.port.out.DeleteUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteUserCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminDisableUserRequestMapper: ToAdminDisableUserRequestMapper,
    private val userPoolProvider: UserPoolProvider
) : DeleteUserPortOut {

    override fun deleteBy(email: Email, type: UserType): Either<ApplicationError, Unit> =
        try {
            buildRequest(email, type).flatMap {
                it.delete().let { Unit.right() }
            }
        } catch (e: Throwable) {
            e.handleCognitoError(email).left()
        }

    private fun buildRequest(email: Email, type: UserType) =
        userPoolProvider.provideFor(type).map {
            toAdminDisableUserRequestMapper.mapFrom(email, it)
        }
            .log { info("request builded: {}", it) }

    private fun AdminDisableUserRequest.delete() =
        awsCognitoIdentityProvider.adminDisableUser(this)
            .log { info("user deleted: {}", it) }

    companion object : CompanionLogger()
}
