package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult
import com.menta.api.users.adapter.out.model.mapper.ToAdminGetUserRequestMapper
import com.menta.api.users.application.port.out.FindUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.mapper.ToUserMapper
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindUserCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminGetUserRequestMapper: ToAdminGetUserRequestMapper,
    private val toUserMapper: ToUserMapper,
    private val userPoolProvider: UserPoolProvider
) : FindUserPortOut {

    override fun findBy(
        email: Email,
        type: UserType
    ): Either<ApplicationError, User> =
        try {
            buildRequest(email, type).map {
                it.find().toUser(type)
            }
        } catch (e: Throwable) {
            e.handleCognitoError(email).left()
        }

    private fun buildRequest(email: Email, type: UserType) =
        userPoolProvider.provideFor(type).map {
            toAdminGetUserRequestMapper.mapFrom(email, it)
        }
            .log { info("request builded: {}", it) }

    private fun AdminGetUserRequest.find() =
        awsCognitoIdentityProvider.adminGetUser(this)
            .log { info("user found: {}", it) }

    private fun AdminGetUserResult.toUser(type: UserType) =
        toUserMapper.mapFrom(this, type)
            .log { info("user mapped: {}", it) }

    companion object : CompanionLogger()
}
