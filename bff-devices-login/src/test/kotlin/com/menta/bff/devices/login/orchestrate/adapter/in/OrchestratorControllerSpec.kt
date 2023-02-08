package com.menta.bff.devices.login.orchestrate.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.bff.devices.login.login.aConfirmRestoreUserPassword
import com.menta.bff.devices.login.login.aConfirmRestoreUserPasswordRequest
import com.menta.bff.devices.login.login.aOrchestratedRestorePasswordRequest
import com.menta.bff.devices.login.login.aOrchestratedRestoreUserPassword
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.aUserCredentials
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedLoginRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedNewPasswordChallengeRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRefreshRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRevokeTokenRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratorResponse
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper.ToConfirmRestoreUserPasswordMapper
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper.ToOrchestratorResponseMapper
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedConfirmPasswordInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedLoginInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedNewPasswordChallengeInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRefreshInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRenewPasswordInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRevokeTokenInPort
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedRestoreUserPasswordMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserCredentialsMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserNewPasswordChallengeMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserRefreshMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserRevokeTokenMapper
import com.menta.bff.devices.login.shared.other.error.ErrorHandler
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.bff.devices.login.shared.other.error.providers.CurrentResourceProvider
import com.menta.bff.devices.login.shared.other.error.providers.ErrorResponseMetadataProvider
import com.menta.bff.devices.login.shared.other.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

class LoginControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val orchestratedLoginInPort = mockk<OrchestratedLoginInPort>()
    val orchestratedRefreshInPort = mockk<OrchestratedRefreshInPort>()
    val orchestratedRevokeTokenInPort = mockk<OrchestratedRevokeTokenInPort>()
    val orchestratedNewPasswordChallengeInPort = mockk<OrchestratedNewPasswordChallengeInPort>()
    val orchestratedRenewPasswordInPort = mockk<OrchestratedRenewPasswordInPort>()
    val orchestratedConfirmPasswordInPort = mockk<OrchestratedConfirmPasswordInPort>()
    val toOrchestratedUserCredentialsMapper = mockk<ToOrchestratedUserCredentialsMapper>()
    val toOrchestratedUserRefreshMapper = mockk<ToOrchestratedUserRefreshMapper>()
    val toOrchestratedUserRevokeTokenMapper = mockk<ToOrchestratedUserRevokeTokenMapper>()
    val toOrchestratedUserNewPasswordChallengeMapper = mockk<ToOrchestratedUserNewPasswordChallengeMapper>()
    val toOrchestratedRestoreUserPasswordMapper = mockk<ToOrchestratedRestoreUserPasswordMapper>()
    val toConfirmRestoreUserPasswordMapper = mockk<ToConfirmRestoreUserPasswordMapper>()
    val toResponseMapper = mockk<ToOrchestratorResponseMapper>()

    val controller = OrchestratorController(
        orchestratedLoginInPort = orchestratedLoginInPort,
        orchestratedRefreshInPort = orchestratedRefreshInPort,
        orchestratedRevokeTokenInPort = orchestratedRevokeTokenInPort,
        orchestratedNewPasswordChallengeInPort = orchestratedNewPasswordChallengeInPort,
        orchestratedRenewPasswordInPort = orchestratedRenewPasswordInPort,
        orchestratedConfirmPasswordInPort = orchestratedConfirmPasswordInPort,
        toOrchestratedUserCredentialsMapper = toOrchestratedUserCredentialsMapper,
        toOrchestratedUserRefreshMapper = toOrchestratedUserRefreshMapper,
        toOrchestratedUserRevokeTokenMapper = toOrchestratedUserRevokeTokenMapper,
        toOrchestratedUserNewPasswordChallengeMapper = toOrchestratedUserNewPasswordChallengeMapper,
        toOrchestratedRestoreUserPasswordMapper = toOrchestratedRestoreUserPasswordMapper,
        toConfirmRestoreUserPasswordMapper = toConfirmRestoreUserPasswordMapper,
        toResponseMapper = toResponseMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(aControllerAdvice(httpServletRequest))
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("orchestrator controller login") {
        val request = OrchestratedLoginRequest(
            user = "user",
            password = "password",
            userType = MERCHANT,
            orchestratedEntities = null
        )
        val userAuth = aUserAuthResponseWithToken()
        val userCredential = aUserCredentials()
        val orchestratedUserCredentials = OrchestratedUserCredentials(
            userCredentials = userCredential,
            orchestratedEntityParameters = null
        )
        val orchestratedAuth = OrchestratedAuth(userAuth = userAuth, orchestratedEntities = null)
        val response =
            OrchestratorResponse(
                auth = userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )

        scenario("successful login") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login"
            every { toOrchestratedUserCredentialsMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedLoginInPort.login(orchestratedUserCredentials) } returns orchestratedAuth.right()
            every { toResponseMapper.mapFrom(orchestratedAuth) } returns response

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestLogin())
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.auth.token.access_token").value(response.auth.token!!.accessToken))
                .andExpect(jsonPath("$.auth.token.expires_in").value(response.auth.token!!.expiresIn))
                .andExpect(jsonPath("$.auth.token.token_type").value(response.auth.token!!.tokenType))
                .andExpect(jsonPath("$.auth.token.refresh_token").value(response.auth.token!!.refreshToken))
                .andExpect(jsonPath("$.auth.token.id_token").value(response.auth.token!!.idToken))
                .andExpect(jsonPath("$.auth.challenge").doesNotExist())
                .andExpect(jsonPath("$.customer").doesNotExist())
                .andExpect(jsonPath("$.merchant").doesNotExist())
                .andExpect(jsonPath("$.workflows").doesNotExist())
                .andExpect(jsonPath("$.acquirers").doesNotExist())

            verify(exactly = 1) { toOrchestratedUserCredentialsMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedLoginInPort.login(orchestratedUserCredentials) }
            verify(exactly = 1) { toResponseMapper.mapFrom(orchestratedAuth) }
        }
        scenario("unsuccessful login") {
            val error = unauthorizedUser()
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login"
            every { toOrchestratedUserCredentialsMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedLoginInPort.login(orchestratedUserCredentials) } returns error.left()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestLogin())
                )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/public/bff-devices-login/login"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("Unauthorized"))

            verify(exactly = 1) { toOrchestratedUserCredentialsMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedLoginInPort.login(orchestratedUserCredentials) }
            verify(exactly = 0) { toResponseMapper.mapFrom(any()) }
        }
    }
    feature("orchestrator controller refresh token") {
        val request = OrchestratedRefreshRequest(
            user = "user",
            userType = MERCHANT,
            refreshToken = "token",
            orchestratedEntities = null
        )
        val userAuth = aUserAuthResponseWithToken()
        val orchestratedUserCredentials = OrchestratedUserRefresh(
            user = "user",
            refresh = Refresh(token = "token", userType = MERCHANT),
            orchestratedEntityParameters = null
        )
        val orchestratedAuth = OrchestratedAuth(userAuth = userAuth, orchestratedEntities = null)
        val response =
            OrchestratorResponse(
                auth = userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )

        scenario("successful refresh") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/refresh"
            every { toOrchestratedUserRefreshMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedRefreshInPort.refresh(orchestratedUserCredentials) } returns orchestratedAuth.right()
            every { toResponseMapper.mapFrom(orchestratedAuth) } returns response

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestRefresh())
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.auth.token.access_token").value(response.auth.token!!.accessToken))
                .andExpect(jsonPath("$.auth.token.expires_in").value(response.auth.token!!.expiresIn))
                .andExpect(jsonPath("$.auth.token.token_type").value(response.auth.token!!.tokenType))
                .andExpect(jsonPath("$.auth.token.refresh_token").value(response.auth.token!!.refreshToken))
                .andExpect(jsonPath("$.auth.token.id_token").value(response.auth.token!!.idToken))
                .andExpect(jsonPath("$.auth.challenge").doesNotExist())
                .andExpect(jsonPath("$.customer").doesNotExist())
                .andExpect(jsonPath("$.merchant").doesNotExist())
                .andExpect(jsonPath("$.workflows").doesNotExist())
                .andExpect(jsonPath("$.acquirers").doesNotExist())

            verify(exactly = 1) { toOrchestratedUserRefreshMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedRefreshInPort.refresh(orchestratedUserCredentials) }
            verify(exactly = 1) { toResponseMapper.mapFrom(orchestratedAuth) }
        }
        scenario("unsuccessful refresh") {
            val error = unauthorizedUser()
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/refresh"
            every { toOrchestratedUserRefreshMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedRefreshInPort.refresh(orchestratedUserCredentials) } returns error.left()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestRefresh())
                )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/public/bff-devices-login/login/refresh"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("Unauthorized"))

            verify(exactly = 1) { toOrchestratedUserRefreshMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedRefreshInPort.refresh(orchestratedUserCredentials) }
            verify(exactly = 0) { toResponseMapper.mapFrom(any()) }
        }
    }

    feature("orchestrator controller revoke token") {
        val request = OrchestratedRevokeTokenRequest(
            user = "user",
            userType = MERCHANT,
            refreshToken = "token"
        )

        val orchestratedUserCredentials = OrchestratedUserRevokeToken(
            user = "user",
            revokeToken = RevokeToken(token = "token", userType = MERCHANT)
        )

        scenario("successful revoke") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/revoke"
            every { toOrchestratedUserRevokeTokenMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedRevokeTokenInPort.revoke(orchestratedUserCredentials) } returns Unit.right()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestRevokeToken())
                )
                .andExpect(status().isNoContent)

            verify(exactly = 1) { toOrchestratedUserRevokeTokenMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedRevokeTokenInPort.revoke(orchestratedUserCredentials) }
        }

        scenario("unsuccessful revoke") {
            val error = unauthorizedUser()
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/revoke"
            every { toOrchestratedUserRevokeTokenMapper.mapFrom(request) } returns orchestratedUserCredentials
            every { orchestratedRevokeTokenInPort.revoke(orchestratedUserCredentials) } returns error.left()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestRevokeToken())
                )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/public/bff-devices-login/login/revoke"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("Unauthorized"))

            verify(exactly = 1) { toOrchestratedUserRevokeTokenMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedRevokeTokenInPort.revoke(orchestratedUserCredentials) }
            verify(exactly = 0) { toResponseMapper.mapFrom(any()) }
        }
    }

    feature("orchestrator controller restore password") {
        val request = aOrchestratedRestorePasswordRequest()
        val entity = aOrchestratedRestoreUserPassword()

        scenario("restore successfully") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/forgot-password"
            every { toOrchestratedRestoreUserPasswordMapper.mapFrom(request) } returns entity
            every { orchestratedRenewPasswordInPort.solve(entity) } returns Unit.right()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestRestorePassword())
                )
                .andExpect(status().isNoContent)
        }
    }

    feature("orchestrator controller confirm password") {
        val request = aConfirmRestoreUserPasswordRequest()
        val entity = aConfirmRestoreUserPassword()

        scenario("confirm successfully") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/forgot-password"
            every { toConfirmRestoreUserPasswordMapper.mapFrom(request) } returns entity
            every { orchestratedConfirmPasswordInPort.confirm(entity) } returns Unit.right()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/confirm-forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestConfirmPassword())
                )
                .andExpect(status().isNoContent)
        }
    }

    feature("orchestrator controller new password challenge") {
        val request = OrchestratedNewPasswordChallengeRequest(
            session = "a session",
            user = "user@user.com",
            userType = MERCHANT,
            newPassword = "a new password",
            orchestratedEntities = null
        )
        val orchestratedUserChallenge = OrchestratedUserChallenge(
            newPasswordChallengeSolution = NewPasswordChallengeSolution(
                session = "a session",
                user = "user@user.com",
                userType = MERCHANT,
                newPassword = "a new password",
            ),
            orchestratedEntityParameters = null
        )
        val userAuth = aUserAuthResponseWithToken()
        val orchestratedAuth = OrchestratedAuth(userAuth = userAuth, orchestratedEntities = null)
        val response = OrchestratorResponse(
            auth = userAuth,
            customer = null,
            merchant = null,
            terminal = null,
            null,
            acquirers = null,
            deviceFlows = null
        )

        scenario("successful resolve challenge") {
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/challenge_solutions/new_password_required"
            every { toOrchestratedUserNewPasswordChallengeMapper.mapFrom(request) } returns orchestratedUserChallenge
            every { orchestratedNewPasswordChallengeInPort.solve(orchestratedUserChallenge) } returns orchestratedAuth.right()
            every { toResponseMapper.mapFrom(orchestratedAuth) } returns response

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/challenge_solutions/new_password_required")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestNewPasswordChallenge())
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.auth.token.access_token").value(response.auth.token!!.accessToken))
                .andExpect(jsonPath("$.auth.token.expires_in").value(response.auth.token!!.expiresIn))
                .andExpect(jsonPath("$.auth.token.token_type").value(response.auth.token!!.tokenType))
                .andExpect(jsonPath("$.auth.token.refresh_token").value(response.auth.token!!.refreshToken))
                .andExpect(jsonPath("$.auth.token.id_token").value(response.auth.token!!.idToken))
                .andExpect(jsonPath("$.auth.challenge").doesNotExist())
                .andExpect(jsonPath("$.customer").doesNotExist())
                .andExpect(jsonPath("$.merchant").doesNotExist())
                .andExpect(jsonPath("$.workflows").doesNotExist())
                .andExpect(jsonPath("$.acquirers").doesNotExist())

            verify(exactly = 1) { toOrchestratedUserNewPasswordChallengeMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedNewPasswordChallengeInPort.solve(orchestratedUserChallenge) }
            verify(exactly = 1) { toResponseMapper.mapFrom(orchestratedAuth) }
        }
        scenario("unsuccessful resolve challenge") {
            val error = unauthorizedUser()
            every { httpServletRequest.requestURI } returns "/public/bff-devices-login/login/challenge_solutions/new_password_required"
            every { toOrchestratedUserNewPasswordChallengeMapper.mapFrom(request) } returns orchestratedUserChallenge
            every { orchestratedNewPasswordChallengeInPort.solve(orchestratedUserChallenge) } returns error.left()

            mockMvc
                .perform(
                    post("/public/bff-devices-login/login/challenge_solutions/new_password_required")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestNewPasswordChallenge())
                )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/public/bff-devices-login/login/challenge_solutions/new_password_required"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("Unauthorized"))

            verify(exactly = 1) { toOrchestratedUserNewPasswordChallengeMapper.mapFrom(request) }
            verify(exactly = 1) { orchestratedNewPasswordChallengeInPort.solve(orchestratedUserChallenge) }
            verify(exactly = 0) { toResponseMapper.mapFrom(any()) }
        }
    }
})

fun aJsonRequestRestorePassword() = """
{
   "user": "user@menta.com",
   "user_type": "MERCHANT"
}
""".trimIndent()

fun aJsonRequestConfirmPassword() = """
{
   "user": "user@menta.com",
   "user_type": "MERCHANT",
   "new_password": "password",
   "code": "123456"
}
""".trimIndent()

fun aJsonRequestLogin() = """
{
   "user": "user",
   "password": "password"
}
""".trimIndent()

fun aJsonRequestRefresh() = """
{
   "user": "user",
   "refresh_token": "token",
   "user_type": "MERCHANT"
}
""".trimIndent()

fun aJsonRequestRevokeToken() = """
{
   "user": "user",
   "refresh_token": "token",
   "user_type": "MERCHANT"
}
""".trimIndent()

fun aJsonRequestNewPasswordChallenge() = """
{
   "session": "a session",
   "user": "user@user.com",
   "new_password": "a new password",
   "user_type": "MERCHANT"
}
""".trimIndent()

fun aControllerAdvice(request: HttpServletRequest) = ErrorHandler(
    errorResponseProvider = ErrorResponseProvider(
        currentResourceProvider = CurrentResourceProvider(request),
        metadataProvider = ErrorResponseMetadataProvider(
            currentResourceProvider = CurrentResourceProvider(request)
        )
    )
)
