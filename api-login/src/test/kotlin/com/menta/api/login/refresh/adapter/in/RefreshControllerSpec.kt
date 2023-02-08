package com.menta.api.login.refresh.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.login.aUserAuthResponseWithChallenge
import com.menta.api.login.aUserAuthResponseWithToken
import com.menta.api.login.aUserAuthWithChallenge
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.login.adapter.`in`.aControllerAdvice
import com.menta.api.login.refresh.aRefresh
import com.menta.api.login.refresh.aRefreshRequest
import com.menta.api.login.refresh.application.port.`in`.RefreshInPort
import com.menta.api.login.refresh.domain.mapper.ToRefreshMapper
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.text.SimpleDateFormat
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import javax.servlet.http.HttpServletRequest

class RefreshControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()

    val refreshInPort = mockk<RefreshInPort>()
    val toRefreshMapper = mockk<ToRefreshMapper>()
    val toUserAuthResponseMapper = mockk<ToUserAuthResponseMapper>()

    val controller = RefreshController(
        refreshInPort = refreshInPort,
        toRefreshMapper = toRefreshMapper,
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

    feature("token refreshed") {

        scenario("token refreshed and returns token") {
            val request = aRefreshRequest()
            val refresh = aRefresh()
            val userAuth = aUserAuthWithToken()
            val userAuthResponse = aUserAuthResponseWithToken()

            every { toRefreshMapper.mapFrom(request) } returns refresh
            every { refreshInPort.refresh(refresh) } returns userAuth.right()
            every { toUserAuthResponseMapper.mapFrom(userAuth) } returns userAuthResponse

            with(userAuthResponse.token!!) {
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post("/public/login/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(jsonPath("$.token.access_token").value(accessToken))
                    .andExpect(jsonPath("$.token.expires_in").value(expiresIn))
                    .andExpect(jsonPath("$.token.refresh_token").value(refreshToken))
                    .andExpect(jsonPath("$.token.id_token").value(idToken))
                    .andExpect(jsonPath("$.challenge").doesNotExist())
            }
            verify(exactly = 1) { toRefreshMapper.mapFrom(request) }
            verify(exactly = 1) { refreshInPort.refresh(refresh) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }

        scenario("token refreshed and returns challenge") {
            val request = aRefreshRequest()
            val refresh = aRefresh()
            val userAuth = aUserAuthWithChallenge()
            val userAuthResponse = aUserAuthResponseWithChallenge()

            every { toRefreshMapper.mapFrom(request) } returns refresh
            every { refreshInPort.refresh(refresh) } returns userAuth.right()
            every { toUserAuthResponseMapper.mapFrom(userAuth) } returns userAuthResponse

            with(userAuthResponse.challenge!!) {
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post("/public/login/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(jsonPath("$.token").doesNotExist())
                    .andExpect(jsonPath("$.challenge.name").value(name.toString()))
                    .andExpect(jsonPath("$.challenge.session").value(session))
                    .andExpect(jsonPath("$.challenge.parameters['USERNAME']").value(parameters[ChallengeAttribute.USERNAME]))
                    .andExpect(jsonPath("$.challenge.parameters['NEW_PASSWORD']").value(parameters[ChallengeAttribute.NEW_PASSWORD]))
            }

            verify(exactly = 1) { toRefreshMapper.mapFrom(request) }
            verify(exactly = 1) { refreshInPort.refresh(refresh) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }

        scenario("token refreshed error") {
            val request = aRefreshRequest()
            val refresh = aRefresh()
            val error = unauthorizedUser()

            every { toRefreshMapper.mapFrom(request) } returns refresh
            every { refreshInPort.refresh(refresh) } returns error.left()

            every { httpServletRequest.requestURI } returns "/public/login/refresh"
            every { httpServletRequest.queryString } returns null

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/public/login/refresh")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(jsonPath("$.errors[0].resource").value("/public/login/refresh"))
                .andExpect(jsonPath("$.errors[0].message").value(error.message))
                .andExpect(jsonPath("$.errors[0].metadata['query_string']").value(""))

            verify(exactly = 1) { toRefreshMapper.mapFrom(request) }
            verify(exactly = 1) { refreshInPort.refresh(refresh) }
            verify(exactly = 0) { toUserAuthResponseMapper.mapFrom(any()) }
        }


    }
})