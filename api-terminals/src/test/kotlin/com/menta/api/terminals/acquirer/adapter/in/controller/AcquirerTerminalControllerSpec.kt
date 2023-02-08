package com.menta.api.terminals.acquirer.adapter.`in`.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.menta.api.terminals.aPreAcquirerTerminal
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.acquirer.adapter.`in`.model.mapper.ToAcquirerTerminalResponseMapper
import com.menta.api.terminals.acquirer.adapter.`in`.model.mapper.ToPreAcquirerTerminalMapper
import com.menta.api.terminals.acquirer.anAcquirer
import com.menta.api.terminals.acquirer.anAcquirerId
import com.menta.api.terminals.acquirer.anAcquirerTerminal
import com.menta.api.terminals.acquirer.anAcquirerTerminalResponse
import com.menta.api.terminals.acquirer.application.port.`in`.FindAcquirerTerminalPortIn
import com.menta.api.terminals.acquirer.domain.provider.AcquirerProvider
import com.menta.api.terminals.anAcquirerTerminalRequest
import com.menta.api.terminals.anApiErrorResponse
import com.menta.api.terminals.applications.port.`in`.CreateAcquirerTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.UpdateAcquirerTerminalPortIn
import com.menta.api.terminals.shared.error.ErrorHandler
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.acquirerTerminalNotFound
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.invalidAcquirer
import com.menta.api.terminals.shared.error.providers.CurrentResourceProvider
import com.menta.api.terminals.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.terminals.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.util.Optional
import javax.servlet.http.HttpServletRequest

class AcquirerTerminalControllerSpec : FeatureSpec({

    val findTerminalPortIn = mockk<FindAcquirerTerminalPortIn>()
    val createTerminalPortIn = mockk<CreateAcquirerTerminalPortIn>()
    val updateTerminalPortIn = mockk<UpdateAcquirerTerminalPortIn>()
    val mapper = mockk<ToAcquirerTerminalResponseMapper>()
    val errorProvider = mockk<ErrorResponseProvider>()
    val acquirerProvider = mockk<AcquirerProvider>()
    val toPreAcquirerTerminalMapper = mockk<ToPreAcquirerTerminalMapper>()

    val controller = AcquirerTerminalController(
        findAcquirerTerminal = findTerminalPortIn,
        toResponseMapper = mapper,
        errorResponseProvider = errorProvider,
        acquirerProvider = acquirerProvider,
        createAcquirerTerminal = createTerminalPortIn,
        updateAcquirerTerminal = updateTerminalPortIn,
        toPreAcquirerTerminalMapper = toPreAcquirerTerminalMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)

    val httpServletRequest = mockk<HttpServletRequest>()
    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(messageConverter)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get terminal by acquirer and terminal id ") {

        val uri = "/private/terminals/$aTerminalId/acquirers/$anAcquirerId"
        every { httpServletRequest.requestURI } returns uri

        scenario("terminal found") {

            every { acquirerProvider.provideFor(anAcquirerId) } returns anAcquirer.right()
            every { findTerminalPortIn.execute(anAcquirerId, aTerminalId) } returns anAcquirerTerminal.right()
            every { mapper.mapFrom(anAcquirerTerminal) } returns anAcquirerTerminalResponse

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.terminal_id").value(anAcquirerTerminalResponse.terminalId.toString()))
                .andExpect(jsonPath("$.acquirer").value(anAcquirerTerminalResponse.acquirer))
                .andExpect(jsonPath("$.code").value(anAcquirerTerminalResponse.code))

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 1) { findTerminalPortIn.execute(anAcquirerId, aTerminalId) }
            verify(exactly = 1) { mapper.mapFrom(anAcquirerTerminal) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("invalid acquirer") {

            val error = invalidAcquirer(anAcquirerId)

            every { acquirerProvider.provideFor(anAcquirerId) } returns error.left()
            every { errorProvider.provideFor(error) } returns anApiErrorResponse

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(anApiErrorResponse.errors[0].code))
                .andExpect(jsonPath("errors[0].resource").value(anApiErrorResponse.errors[0].resource))
                .andExpect(jsonPath("errors[0].metadata.a_key").value(anApiErrorResponse.errors[0].metadata["a_key"]))
                .andExpect(jsonPath("errors[0].message").value(anApiErrorResponse.errors[0].message))

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 0) { findTerminalPortIn.execute(anAcquirerId, aTerminalId) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
            verify(exactly = 1) { errorProvider.provideFor(error) }
        }

        scenario("terminal NOT found") {

            val error = acquirerTerminalNotFound(anAcquirerId, aTerminalId)

            every { acquirerProvider.provideFor(anAcquirerId) } returns anAcquirer.right()
            every { findTerminalPortIn.execute(anAcquirerId, aTerminalId) } returns error.left()
            every { errorProvider.provideFor(error) } returns anApiErrorResponse

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(anApiErrorResponse.errors[0].code))
                .andExpect(jsonPath("errors[0].resource").value(anApiErrorResponse.errors[0].resource))
                .andExpect(jsonPath("errors[0].metadata.a_key").value(anApiErrorResponse.errors[0].metadata["a_key"]))
                .andExpect(jsonPath("errors[0].message").value(anApiErrorResponse.errors[0].message))

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 1) { findTerminalPortIn.execute(anAcquirerId, aTerminalId) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
            verify(exactly = 1) { errorProvider.provideFor(error) }
        }
    }

    feature("create new acquirer terminal") {

        val uri = "/private/terminals/$aTerminalId/acquirers/"
        every { httpServletRequest.requestURI } returns uri

        scenario("with acquirer terminal") {

            every { findTerminalPortIn.find(aTerminalId, "GPS") } returns Optional.empty()
            every { acquirerProvider.provideFor("GPS") } returns anAcquirer.right()
            every {
                toPreAcquirerTerminalMapper.map(
                    anAcquirerTerminalRequest,
                    aTerminalId
                )
            } returns aPreAcquirerTerminal
            every { mapper.mapFrom(anAcquirerTerminal) } returns anAcquirerTerminalResponse
            every {
                createTerminalPortIn.execute(
                    aPreAcquirerTerminal,
                    Optional.empty()
                )
            } returns anAcquirerTerminal.right()

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post(uri)
                    .contentType(APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(anAcquirerTerminalRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.terminal_id").value(anAcquirerTerminalResponse.terminalId.toString()))
                .andExpect(jsonPath("$.acquirer").value(anAcquirerTerminalResponse.acquirer))
                .andExpect(jsonPath("$.code").value(anAcquirerTerminalResponse.code))

            verify(exactly = 1) { createTerminalPortIn.execute(aPreAcquirerTerminal, Optional.empty()) }
            verify(exactly = 1) { findTerminalPortIn.find(aTerminalId, "GPS") }
            verify(exactly = 1) { acquirerProvider.provideFor("GPS") }
            verify(exactly = 1) { toPreAcquirerTerminalMapper.map(anAcquirerTerminalRequest, aTerminalId) }
            verify(exactly = 1) { mapper.mapFrom(anAcquirerTerminal) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }

    feature("update acquirer terminal") {

        val uri = "/private/terminals/$aTerminalId/acquirers/"
        every { httpServletRequest.requestURI } returns uri

        scenario("with acquirer terminal") {

            every { findTerminalPortIn.find(aTerminalId, "GPS") } returns Optional.of(anAcquirerTerminal)
            every { acquirerProvider.provideFor("GPS") } returns anAcquirer.right()
            every {
                toPreAcquirerTerminalMapper.map(
                    anAcquirerTerminalRequest,
                    aTerminalId
                )
            } returns aPreAcquirerTerminal
            every { mapper.mapFrom(anAcquirerTerminal) } returns anAcquirerTerminalResponse
            every {
                updateTerminalPortIn.execute(
                    aPreAcquirerTerminal,
                    Optional.of(anAcquirerTerminal)
                )
            } returns anAcquirerTerminal.right()

            mockMvc.perform(
                MockMvcRequestBuilders
                    .put(uri)
                    .contentType(APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(anAcquirerTerminalRequest)
                    )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.terminal_id").value(anAcquirerTerminalResponse.terminalId.toString()))
                .andExpect(jsonPath("$.acquirer").value(anAcquirerTerminalResponse.acquirer))
                .andExpect(jsonPath("$.code").value(anAcquirerTerminalResponse.code))

            verify(exactly = 1) { updateTerminalPortIn.execute(aPreAcquirerTerminal, Optional.of(anAcquirerTerminal)) }
            verify(exactly = 1) { findTerminalPortIn.find(aTerminalId, "GPS") }
            verify(exactly = 1) { acquirerProvider.provideFor("GPS") }
            verify(exactly = 1) { toPreAcquirerTerminalMapper.map(anAcquirerTerminalRequest, aTerminalId) }
            verify(exactly = 1) { mapper.mapFrom(anAcquirerTerminal) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }
})

fun aControllerAdvice(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )
