package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult
import com.menta.api.users.adapter.out.model.mapper.ToAdminCreateUserRequestMapper
import com.menta.api.users.application.port.out.CreateUserPortOut
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.mapper.ToUserMapper
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateUserCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminCreateUserRequestMapper: ToAdminCreateUserRequestMapper,
    private val toUserMapper: ToUserMapper,
    private val userPoolProvider: UserPoolProvider
) : CreateUserPortOut {

    override fun create(newUser: NewUser): Either<ApplicationError, User> =
        try {
            newUser.toRequest().map {
                it.doCreate()
                    .toUser(newUser.type)
            }
        } catch (e: Throwable) {
            e.handleCognitoError(newUser.attributes.email).left()
        }

    private fun NewUser.toRequest() =
        userPoolProvider.provideFor(type).map {
            toAdminCreateUserRequestMapper.mapFrom(this, it)
        }.logRight { info("request mapped: {}", it) }

    private fun AdminCreateUserRequest.doCreate() =
        awsCognitoIdentityProvider.adminCreateUser(this)
            .log { info("user created: {}", it) }

    private fun AdminCreateUserResult.toUser(type: UserType) =
        toUserMapper.mapFrom(this, type)
            .log { info("user mapped from result: {}", it) }

    companion object : CompanionLogger()
}
