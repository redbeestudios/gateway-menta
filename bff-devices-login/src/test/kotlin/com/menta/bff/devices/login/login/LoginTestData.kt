package com.menta.bff.devices.login.login

import com.menta.bff.devices.login.entities.user.adapter.out.models.ConfirmPasswordUserRequest
import com.menta.bff.devices.login.login.auth.adapter.out.model.LoginClientRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.ConfirmRestoreUserPasswordRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRestorePasswordRequest
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.bff.devices.login.shared.domain.ChallengeAttribute.USERNAME
import com.menta.bff.devices.login.shared.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApiError
import com.menta.bff.devices.login.shared.other.error.model.ApiErrorResponse
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))
val refreshToken = OffsetDateTime.now().plusDays(1).toString()
const val USER = "user@menta.com"

fun aUserCredentials() = UserCredentials(
    user = "user",
    password = "password",
    userType = MERCHANT
)

fun aLoginRequest() =
    LoginClientRequest(
        user = "user",
        password = "password",
        userType = MERCHANT
    )

fun aOrchestratedRestoreUserPassword() =
    OrchestratedRestoreUserPassword(
        user = USER,
        userType = MERCHANT
    )

fun aOrchestratedRestorePasswordRequest() =
    OrchestratedRestorePasswordRequest(
        user = USER,
        userType = MERCHANT
    )

fun aConfirmPasswordUserRequest() =
    ConfirmPasswordUserRequest(
        code = "123456",
        password = "password"
    )

fun aConfirmRestoreUserPassword() =
    ConfirmRestoreUserPassword(
        code = "123456",
        user = USER,
        userType = MERCHANT,
        newPassword = "password"
    )

fun aConfirmRestoreUserPasswordRequest() =
    ConfirmRestoreUserPasswordRequest(
        code = "123456",
        user = USER,
        userType = MERCHANT,
        newPassword = "password"
    )

fun aUserAuthResponseWithToken() =
    UserAuth(
        token = aResponseToken(),
        challenge = null
    )

fun aUserAuthResponseWithChallenge() =
    UserAuth(
        token = null,
        challenge = aResponseChallenge()
    )

fun aResponseToken() =
    UserAuth.Token(
        accessToken = "token-123",
        expiresIn = 100,
        tokenType = "TYPE",
        refreshToken = refreshToken,
        idToken = "idToken"
    )

fun aResponseChallenge() =
    UserAuth.Challenge(
        name = NEW_PASSWORD_REQUIRED,
        parameters = mapOf(
            NEW_PASSWORD to "a new password",
            USERNAME to "a username"
        ),
        session = "a session"
    )
fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = datetime,
        errors = listOf(
            ApiError(
                resource = "/",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )
