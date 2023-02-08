package com.menta.api.login

import UserCredentials
import com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.amazonaws.services.cognitoidp.model.CodeMismatchException
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import com.amazonaws.services.cognitoidp.model.InternalErrorException
import com.amazonaws.services.cognitoidp.model.InvalidLambdaResponseException
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException
import com.amazonaws.services.cognitoidp.model.InvalidSmsRoleAccessPolicyException
import com.amazonaws.services.cognitoidp.model.InvalidSmsRoleTrustRelationshipException
import com.amazonaws.services.cognitoidp.model.InvalidUserPoolConfigurationException
import com.amazonaws.services.cognitoidp.model.MFAMethodNotFoundException
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException
import com.amazonaws.services.cognitoidp.model.PasswordResetRequiredException
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.amazonaws.services.cognitoidp.model.SoftwareTokenMFANotFoundException
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException
import com.amazonaws.services.cognitoidp.model.UnexpectedLambdaException
import com.amazonaws.services.cognitoidp.model.UserLambdaValidationException
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException
import com.amazonaws.services.cognitoidp.model.UserNotFoundException
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.login.adapter.`in`.model.LoginRequest
import com.menta.api.login.shared.adapter.out.mapper.ToInitiateAuthRequestMapper.Companion.PASSWORD_ENTRY_KEY
import com.menta.api.login.shared.adapter.out.mapper.ToInitiateAuthRequestMapper.Companion.USERNAME_ENTRY_KEY
import com.menta.api.login.shared.adapter.`in`.model.UserAuthResponse
import com.menta.api.login.shared.domain.UserType.MERCHANT
import com.menta.api.login.shared.other.error.model.ApiError
import com.menta.api.login.shared.other.error.model.ApiErrorResponse
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.passwordResetRequired
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.userNotConfirmed
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))
val refreshToken = OffsetDateTime.now().plusDays(1).toString()

fun aLoginRequest() =
    LoginRequest(
        user = "user",
        password = "password",
        userType = MERCHANT
    )

fun aUserAuthResponseWithToken() =
    UserAuthResponse(
        token = aResponseToken(),
        challenge = null
    )

fun aUserAuthResponseWithChallenge() =
    UserAuthResponse(
        token = null,
        challenge = aResponseChallenge()
    )

fun aResponseToken() =
    UserAuthResponse.Token(
        accessToken = "token-123",
        expiresIn = 100,
        tokenType = "TYPE",
        refreshToken = refreshToken,
        idToken = "idToken"
    )

fun aResponseChallenge() =
    UserAuthResponse.Challenge(
        name = NEW_PASSWORD_REQUIRED,
        parameters = mapOf(
            NEW_PASSWORD to "a new password",
            USERNAME to "a username"
        ),
        session = "a session"
    )

fun aUserCredentials() =
    UserCredentials(
        user = "user",
        password = "password",
        userType = MERCHANT
    )


fun aInitiateAuthRequest(): InitiateAuthRequest = InitiateAuthRequest()
    .withAuthFlow(USER_PASSWORD_AUTH)
    .withClientId("4bo11klmou1r2ujqm227p086os")
    .addAuthParametersEntry(USERNAME_ENTRY_KEY, "user")
    .addAuthParametersEntry(PASSWORD_ENTRY_KEY, "password")

fun aInitiateAuthResult(): InitiateAuthResult = InitiateAuthResult()
    .withAuthenticationResult(
        AuthenticationResultType()
            .withAccessToken("token-123")
            .withExpiresIn(100)
            .withTokenType("TYPE")
            .withRefreshToken(refreshToken)
            .withIdToken("idToken")
    )
    .withChallengeParameters(hashMapOf())

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


val exceptionToApplicationErrorTable =
    table(
        headers("exception", "applicationError"),
        InvalidParameterException("an invalid parameter exception").asRow { unauthorizedUser(it) },
        NotAuthorizedException("a not authorized exception").asRow { unauthorizedUser(it) },
        UserNotFoundException("a user not found exception").asRow { unauthorizedUser(it) },
        ResourceNotFoundException("a resource not found exception").asRow { unauthorizedUser(it) },
        InvalidPasswordException("a invalid password exception").asRow { unauthorizedUser(it) },
        PasswordResetRequiredException("a password reset required exception").asRow { passwordResetRequired() },
        UserNotConfirmedException("a password reset required exception").asRow { userNotConfirmed() },
        UserLambdaValidationException("a user lambda validation exception").asRow { unhandledException(it) },
        ExpiredCodeException("a expired code exception").asRow { unhandledException(it) },
        SoftwareTokenMFANotFoundException("a software token mfa not found exception").asRow { unhandledException(it) },
        InternalErrorException("a internal error exception").asRow { unhandledException(it) },
        InvalidSmsRoleTrustRelationshipException("a invalid sms role trust relationship exception").asRow { unhandledException(it) },
        InvalidSmsRoleAccessPolicyException("a invalid sms role access policy exception").asRow { unhandledException(it) },
        MFAMethodNotFoundException("a mfa method not found exception").asRow { unhandledException(it) },
        InvalidUserPoolConfigurationException("a invalid user pool configuration exception").asRow { unhandledException(it) },
        TooManyRequestsException("a too many request exception").asRow { unhandledException(it) },
        InvalidLambdaResponseException("an invalid lambda response exception").asRow { unhandledException(it) },
        UnexpectedLambdaException("an unexpected lambda exception").asRow { unhandledException(it) },
        CodeMismatchException("a code mismatch exception").asRow { unhandledException(it) }
        )

private fun Throwable.asRow(applicationErrorProvider: (Throwable) -> ApplicationError) =
    row(this, applicationErrorProvider(this))