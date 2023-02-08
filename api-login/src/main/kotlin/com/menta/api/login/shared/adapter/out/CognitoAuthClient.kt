package com.menta.api.login.shared.adapter.out

import UserCredentials
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import com.menta.api.login.login.application.port.out.LoginClientOutPort
import com.menta.api.login.refresh.application.port.`in`.RefreshOutPort
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import com.menta.api.login.shared.adapter.out.mapper.ToInitiateAuthRequestMapper
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.domain.mapper.ToUserAuthMapper
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.handleCognitoError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import com.menta.api.login.shared.other.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class CognitoAuthClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toUserAuth: ToUserAuthMapper,
    private val toInitiateAuthRequest: ToInitiateAuthRequestMapper
) : LoginClientOutPort, RefreshOutPort {
    override fun login(userCredentials: UserCredentials): Either<ApplicationError, UserAuth> =
        userCredentials.toRequest().flatMap { it.initiateAuth() }

    override fun refresh(refresh: UserPoolAwareRefresh): Either<ApplicationError, UserAuth> =
        refresh.toRequest().initiateAuth()

    private fun InitiateAuthRequest.initiateAuth(): Either<ApplicationError, UserAuth> =
        log.benchmark("initiate auth: $this") {
            try {
                doInitiateAuth()
                    .toUserAuth()
                    .right()
            } catch (e: Throwable) {
                e.handleCognitoError().left()
            }
        }

    private fun UserCredentials.toRequest() =
        toInitiateAuthRequest.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun UserPoolAwareRefresh.toRequest() =
        toInitiateAuthRequest.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun InitiateAuthRequest.doInitiateAuth() =
        awsCognitoIdentityProvider.initiateAuth(this)
            .log { info("login result: {}", it) }

    private fun InitiateAuthResult.toUserAuth() =
        toUserAuth.mapFrom(this)
            .log { info("user token mapped: {}", it) }

    companion object : CompanionLogger()
}
