package com.menta.api.login.shared.adapter.out.mapper

import UserCredentials
import arrow.core.Either
import com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN
import com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToInitiateAuthRequestMapper(
    private val userPoolProvider: UserPoolProvider
) {

    fun mapFrom(userCredentials: UserCredentials): Either<ApplicationError, InitiateAuthRequest> =
        with(userCredentials) {
            findClientId().map { clientId ->
                InitiateAuthRequest()
                    .withAuthFlow(USER_PASSWORD_AUTH)
                    .withClientId(clientId)
                    .addAuthParametersEntry(USERNAME_ENTRY_KEY, user)
                    .addAuthParametersEntry(PASSWORD_ENTRY_KEY, password)
            }
        }

    fun mapFrom(userPoolAwareRefresh: UserPoolAwareRefresh): InitiateAuthRequest =
        with(userPoolAwareRefresh) {
            InitiateAuthRequest()
                .withAuthFlow(REFRESH_TOKEN)
                .withClientId(userPool.clientId)
                .addAuthParametersEntry(REFRESH_TOKEN_KEY, refresh.token)
        }.log { info("map refresh to initiate auth request: {}", it) }

    private fun UserCredentials.findClientId() =
        userPoolProvider.provideFor(userType).map { it.clientId }

    companion object : CompanionLogger() {
        const val USERNAME_ENTRY_KEY = "USERNAME"
        const val PASSWORD_ENTRY_KEY = "PASSWORD"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
    }
}
