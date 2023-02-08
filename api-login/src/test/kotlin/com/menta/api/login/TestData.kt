package com.menta.api.login

import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties

fun aUserAuthWithToken() =
    UserAuth(
        token = aToken(),
        challenge = null
    )

fun aUserAuthWithChallenge() =
    UserAuth(
        token = null,
        challenge = aChallenge()
    )

fun aToken() =
    UserAuth.Token(
        accessToken = "token-123",
        expiresIn = 100,
        tokenType = "TYPE",
        refreshToken = refreshToken,
        idToken = "idToken"
    )

fun aChallenge() =
    UserAuth.Challenge(
        name = ChallengeName.NEW_PASSWORD_REQUIRED,
        parameters = mapOf(
            ChallengeAttribute.NEW_PASSWORD to "a new password",
            ChallengeAttribute.USERNAME to "a username"
        ),
        session = "a session"
    )

fun aUserPool() =
    CognitoConfigurationProperties.Provider.UserPool(
        code = "us-east-1_PWeF8HOR0",
        clientId = "4bo11klmou1r2ujqm227p086os"
    )
