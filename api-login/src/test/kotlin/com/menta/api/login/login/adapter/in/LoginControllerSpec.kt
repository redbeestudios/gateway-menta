package com.menta.api.login.login.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.login.aLoginRequest
import com.menta.api.login.aUserAuthResponseWithToken
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.aUserCredentials
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.login.application.usecase.LoginUseCase
import com.menta.api.login.login.domain.mapper.ToUserCredentialsMapper
import com.menta.api.login.shared.other.error.ErrorHandler
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.authenticationProviderError
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.login.shared.other.error.providers.CurrentResourceProvider
import com.menta.api.login.shared.other.error.providers.ErrorResponseMetadataProvider
import com.menta.api.login.shared.other.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

class LoginControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val loginUseCase = mockk<LoginUseCase>()
    val toUserCredentials = mockk<ToUserCredentialsMapper>()
    val toUserAuthResponseMapper = mockk<ToUserAuthResponseMapper>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()
    val errorResponseProvider = ErrorResponseProvider(currentResourceProvider, metadataProvider)

    val controller = LoginController(
        loginInPort = loginUseCase,
        toUserCredentials = toUserCredentials,
        toUserAuthResponseMapper = toUserAuthResponseMapper
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

    feature("login controller") {
        val loginRequest = aLoginRequest()
        val userCredentials = aUserCredentials()
        val userToken = aUserAuthWithToken()
        val loginResponse = aUserAuthResponseWithToken()

        scenario("successful login") {
            every { httpServletRequest.requestURI } returns "/public/login"
            every { toUserCredentials.mapFrom(loginRequest) } returns userCredentials
            every { loginUseCase.login(userCredentials) } returns userToken.right()
            every { toUserAuthResponseMapper.mapFrom(userToken) } returns loginResponse

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.token.access_token").value(loginResponse.token!!.accessToken)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.expires_in").value(loginResponse.token!!.expiresIn))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.token_type").value(loginResponse.token!!.tokenType))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.token.refresh_token").value(loginResponse.token!!.refreshToken)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.id_token").value(loginResponse.token!!.idToken))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.challenge").doesNotExist()
                )

            verify(exactly = 1) { toUserCredentials.mapFrom(loginRequest) }
            verify(exactly = 1) { loginUseCase.login(userCredentials) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userToken) }
        }
        scenario("successful login with default type") {
            every { httpServletRequest.requestURI } returns "/public/login"
            every { toUserCredentials.mapFrom(loginRequest) } returns userCredentials
            every { loginUseCase.login(userCredentials) } returns userToken.right()
            every { toUserAuthResponseMapper.mapFrom(userToken) } returns loginResponse

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestUserDefault())
                )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.token.access_token").value(loginResponse.token!!.accessToken)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.expires_in").value(loginResponse.token!!.expiresIn))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.token_type").value(loginResponse.token!!.tokenType))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.token.refresh_token").value(loginResponse.token!!.refreshToken)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.token.id_token").value(loginResponse.token!!.idToken))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.challenge").doesNotExist()
                )

            verify(exactly = 1) { toUserCredentials.mapFrom(loginRequest) }
            verify(exactly = 1) { loginUseCase.login(userCredentials) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userToken) }
        }
        scenario("unsuccessful login by unauthorized user") {
            val ex = mockk<Throwable>()
            every { httpServletRequest.requestURI } returns "/public/login"
            every { toUserCredentials.mapFrom(loginRequest) } returns userCredentials
            every { loginUseCase.login(userCredentials) } returns unauthorizedUser(ex).left()

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("datetime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].resource").value("/public/login"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").exists())

            verify(exactly = 1) { toUserCredentials.mapFrom(loginRequest) }
            verify(exactly = 1) { loginUseCase.login(userCredentials) }
            verify(exactly = 0) { toUserAuthResponseMapper.mapFrom(any()) }
        }
        scenario("unsuccessful login by authentication provider error") {
            val ex = mockk<Throwable>()
            every { httpServletRequest.requestURI } returns "/public/login"
            every { toUserCredentials.mapFrom(loginRequest) } returns userCredentials
            every { loginUseCase.login(userCredentials) } returns authenticationProviderError(ex).left()

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.jsonPath("datetime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].resource").value("/public/login"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").exists())

            verify(exactly = 1) { toUserCredentials.mapFrom(loginRequest) }
            verify(exactly = 1) { loginUseCase.login(userCredentials) }
            verify(exactly = 0) { toUserAuthResponseMapper.mapFrom(any()) }
        }
        scenario("unsuccessful login by userType") {
            every { httpServletRequest.requestURI } returns "/public/login"

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestUserBadUserType())
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("datetime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].resource").value("/public/login"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").value("Invalid user_type received: unknownType, valid user_type: [MERCHANT, CUSTOMER, SUPPORT]"))

            verify(exactly = 0) { toUserCredentials.mapFrom(any()) }
            verify(exactly = 0) { loginUseCase.login(any()) }
            verify(exactly = 0) { toUserAuthResponseMapper.mapFrom(any()) }
        }
    }
})

fun aJsonRequestUserDefault() = """
{
   "user": "user",
   "password": "password"
}
""".trimIndent()

fun aJsonRequestUserBadUserType() = """
{
   "user": "user",
   "password": "password",
   "user_type": "unknownType"
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
