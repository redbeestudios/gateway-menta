package com.menta.api.login.challenge.adapter.`in`

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
import com.menta.api.login.challenge.aChallengeSolution
import com.menta.api.login.challenge.aChallengeSolutionRequest
import com.menta.api.login.challenge.aNewPasswordRequiredChallengeRequest
import com.menta.api.login.challenge.adapter.`in`.model.mapper.ToChallengeSolutionRequestMapper
import com.menta.api.login.challenge.application.port.`in`.ChallengeSolutionInPort
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.mapper.ToChallengeSolutionMapper
import com.menta.api.login.login.adapter.`in`.aControllerAdvice
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.passwordResetRequired
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import javax.servlet.http.HttpServletRequest

class ChallengeSolutionControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()

    val challengeSolutionInPort = mockk<ChallengeSolutionInPort>()
    val toChallengeSolutionMapper = mockk<ToChallengeSolutionMapper>()
    val toUserAuthResponseMapper = mockk<ToUserAuthResponseMapper>()
    val toChallengeSolutionRequestMapper = mockk<ToChallengeSolutionRequestMapper>()

    val controller = ChallengeSolutionController(
        challengeSolutionInPort = challengeSolutionInPort,
        toChallengeSolutionMapper = toChallengeSolutionMapper,
        toUserAuthResponseMapper = toUserAuthResponseMapper,
        toChallengeSolutionRequestMapper = toChallengeSolutionRequestMapper
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

    feature("solve any challenge") {

        scenario("challenge solved") {
            val request = aChallengeSolutionRequest()
            val challengeSolution = aChallengeSolution()
            val userAuth = aUserAuthWithToken()
            val userAuthResponse = aUserAuthResponseWithToken()

            every { toChallengeSolutionMapper.mapFrom(request) } returns challengeSolution
            every { challengeSolutionInPort.solveChallenge(challengeSolution) } returns userAuth.right()
            every { toUserAuthResponseMapper.mapFrom(userAuth) } returns userAuthResponse

            with(userAuthResponse.token!!) {
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post("/private/login/challenge_solutions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk)
                    .andExpect(jsonPath("$.token.access_token").value(accessToken))
                    .andExpect(jsonPath("$.token.expires_in").value(expiresIn))
                    .andExpect(jsonPath("$.token.refresh_token").value(refreshToken))
                    .andExpect(jsonPath("$.token.id_token").value(idToken))
                    .andExpect(jsonPath("$.challenge").doesNotExist())
            }
            verify(exactly = 1) { toChallengeSolutionMapper.mapFrom(request) }
            verify(exactly = 1) { challengeSolutionInPort.solveChallenge(challengeSolution) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }
    }

    feature("solve new password required challenge") {
        scenario("solve new password required challenge with token response") {
            val request = aNewPasswordRequiredChallengeRequest()
            val genericRequest = aChallengeSolutionRequest()
            val challengeSolution = aChallengeSolution()
            val userAuth = aUserAuthWithToken()
            val userAuthResponse = aUserAuthResponseWithToken()

            every { toChallengeSolutionRequestMapper.mapFrom(request) } returns genericRequest
            every { toChallengeSolutionMapper.mapFrom(genericRequest) } returns challengeSolution
            every { challengeSolutionInPort.solveChallenge(challengeSolution) } returns userAuth.right()
            every { toUserAuthResponseMapper.mapFrom(userAuth) } returns userAuthResponse

            with(userAuthResponse.token!!) {
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post("/public/login/challenge_solutions/new_password_required")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk)
                    .andExpect(jsonPath("$.token.access_token").value(accessToken))
                    .andExpect(jsonPath("$.token.expires_in").value(expiresIn))
                    .andExpect(jsonPath("$.token.refresh_token").value(refreshToken))
                    .andExpect(jsonPath("$.token.id_token").value(idToken))
                    .andExpect(jsonPath("$.challenge").doesNotExist())
            }
            verify(exactly = 1) { toChallengeSolutionRequestMapper.mapFrom(request) }
            verify(exactly = 1) { toChallengeSolutionMapper.mapFrom(genericRequest) }
            verify(exactly = 1) { challengeSolutionInPort.solveChallenge(challengeSolution) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }


        scenario("solve new password required challenge with challenge response") {
            val request = aNewPasswordRequiredChallengeRequest()
            val genericRequest = aChallengeSolutionRequest()
            val challengeSolution = aChallengeSolution()
            val userAuth = aUserAuthWithChallenge()
            val userAuthResponse = aUserAuthResponseWithChallenge()

            every { toChallengeSolutionRequestMapper.mapFrom(request) } returns genericRequest
            every { toChallengeSolutionMapper.mapFrom(genericRequest) } returns challengeSolution
            every { challengeSolutionInPort.solveChallenge(challengeSolution) } returns userAuth.right()
            every { toUserAuthResponseMapper.mapFrom(userAuth) } returns userAuthResponse

            with(userAuthResponse.challenge!!) {
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post("/public/login/challenge_solutions/new_password_required")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk)
                    .andExpect(jsonPath("$.token").doesNotExist())
                    .andExpect(jsonPath("$.challenge.name").value(name.toString()))
                    .andExpect(jsonPath("$.challenge.session").value(session))
                    .andExpect(jsonPath("$.challenge.parameters['USERNAME']").value(parameters[USERNAME]))
                    .andExpect(jsonPath("$.challenge.parameters['NEW_PASSWORD']").value(parameters[NEW_PASSWORD]))
            }

            verify(exactly = 1) { toChallengeSolutionRequestMapper.mapFrom(request) }
            verify(exactly = 1) { toChallengeSolutionMapper.mapFrom(genericRequest) }
            verify(exactly = 1) { challengeSolutionInPort.solveChallenge(challengeSolution) }
            verify(exactly = 1) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }

        scenario("error solving challenge") {
            val request = aNewPasswordRequiredChallengeRequest()
            val genericRequest = aChallengeSolutionRequest()
            val challengeSolution = aChallengeSolution()
            val userAuth = aUserAuthWithChallenge()
            val error = passwordResetRequired()

            every { toChallengeSolutionRequestMapper.mapFrom(request) } returns genericRequest
            every { toChallengeSolutionMapper.mapFrom(genericRequest) } returns challengeSolution
            every { challengeSolutionInPort.solveChallenge(challengeSolution) } returns error.left()
            every { httpServletRequest.requestURI } returns "/public/login/challenge_solutions/new_password_required"
            every { httpServletRequest.queryString } returns null

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/public/login/challenge_solutions/new_password_required")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("$.errors[0].resource").value("/public/login/challenge_solutions/new_password_required"))
                .andExpect(jsonPath("$.errors[0].message").value(error.message))
                .andExpect(jsonPath("$.errors[0].metadata['query_string']").value(""))

            verify(exactly = 1) { toChallengeSolutionRequestMapper.mapFrom(request) }
            verify(exactly = 1) { toChallengeSolutionMapper.mapFrom(genericRequest) }
            verify(exactly = 1) { challengeSolutionInPort.solveChallenge(challengeSolution) }
            verify(exactly = 0) { toUserAuthResponseMapper.mapFrom(userAuth) }
        }
    }

})