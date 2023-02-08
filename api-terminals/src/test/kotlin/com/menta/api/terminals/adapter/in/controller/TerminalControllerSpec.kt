package com.menta.api.terminals.adapter.`in`.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.menta.api.terminals.aPreTerminal
import com.menta.api.terminals.aSerialCode
import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalDelete
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.aTerminalModel
import com.menta.api.terminals.aTerminalRequest
import com.menta.api.terminals.aTerminalResponse
import com.menta.api.terminals.adapter.`in`.model.hateos.TerminalModel
import com.menta.api.terminals.adapter.`in`.model.mapper.ToPagedTerminalMapper
import com.menta.api.terminals.adapter.`in`.model.mapper.ToPreTerminalMapper
import com.menta.api.terminals.adapter.`in`.model.mapper.ToTerminalResponseMapper
import com.menta.api.terminals.anApiErrorResponse
import com.menta.api.terminals.applications.port.`in`.CreateTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.DeleteTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.FindTerminalByFilterPortIn
import com.menta.api.terminals.applications.port.`in`.FindTerminalPortIn
import com.menta.api.terminals.shared.error.ErrorHandler
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalNotFound
import com.menta.api.terminals.shared.error.providers.CurrentResourceProvider
import com.menta.api.terminals.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.terminals.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONArray
import org.springframework.data.domain.PageImpl
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.PagedModel.PageMetadata
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.util.Optional
import java.util.UUID
import javax.servlet.http.HttpServletRequest


class TerminalControllerSpec : FeatureSpec({

    val findTerminalPortIn = mockk<FindTerminalPortIn>()
    val findTerminalByFilter = mockk<FindTerminalByFilterPortIn>()
    val createTerminalPortIn = mockk<CreateTerminalPortIn>()
    val deleteTerminalPortIn = mockk<DeleteTerminalPortIn>()
    val mapper = mockk<ToTerminalResponseMapper>()
    val errorProvider = mockk<ErrorResponseProvider>()
    val toPreTerminalMapper = mockk<ToPreTerminalMapper>()
    val toPagedTerminalMapper = mockk<ToPagedTerminalMapper>()

    val controller = TerminalController(
        findTerminal = findTerminalPortIn,
        findTerminalByFilter = findTerminalByFilter,
        createTerminalPortIn = createTerminalPortIn,
        deleteTerminalPortIn = deleteTerminalPortIn,
        toResponseMapper = mapper,
        toPreTerminalMapper = toPreTerminalMapper,
        errorResponseProvider = errorProvider,
        toPagedTerminalMapper = toPagedTerminalMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
        .build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val httpServletRequest = mockk<HttpServletRequest>()
    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get terminal by terminal id") {

        val uri = "/private/terminals/$aTerminalId"
        every { httpServletRequest.requestURI } returns uri

        scenario("terminal found") {

            every { findTerminalPortIn.execute(aTerminalId) } returns aTerminal.right()
            every { mapper.mapFrom(aTerminal) } returns aTerminalResponse

            mockMvc.perform(
                get(uri)
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(aTerminalResponse.id.toString()))
                .andExpect(jsonPath("$.merchant_id").value(aTerminalResponse.merchantId.toString()))
                .andExpect(jsonPath("$.customer_id").value(aTerminalResponse.customerId.toString()))
                .andExpect(jsonPath("$.serial_code").value(aTerminalResponse.serialCode))
                .andExpect(jsonPath("$.hardware_version").value(aTerminalResponse.hardwareVersion))
                .andExpect(jsonPath("$.trade_mark").value(aTerminalResponse.tradeMark))
                .andExpect(jsonPath("$.model").value(aTerminalResponse.model))
                .andExpect(jsonPath("$.status").value(aTerminalResponse.status.name))
                .andExpect(jsonPath("$.features[0]").value(aTerminalResponse.features[0].name))
                .andExpect(jsonPath("$.features[1]").value(aTerminalResponse.features[1].name))
                .andExpect(jsonPath("$.features[2]").value(aTerminalResponse.features[2].name))
                .andExpect(jsonPath("$.features[3]").value(aTerminalResponse.features[3].name))

            verify(exactly = 1) { findTerminalPortIn.execute(aTerminalId) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("terminal NOT found") {

            val error = terminalNotFound(aTerminalId)

            every { findTerminalPortIn.execute(aTerminalId) } returns error.left()
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

            verify(exactly = 1) { findTerminalPortIn.execute(aTerminalId) }
            verify(exactly = 1) { errorProvider.provideFor(error) }
        }
    }

    feature("get terminal by filter") {

        scenario("terminal found with serial code") {
            val uri = "/private/terminals?serialCode=$aSerialCode"
            val result: PagedModel<TerminalModel> =
                PagedModel.of(listOf(aTerminalModel), PageMetadata(10, 1, 1, 1))
            val pageTerminal = PageImpl(listOf(aTerminal))

            every { httpServletRequest.requestURI } returns uri
            every {
                findTerminalByFilter.execute(
                    aSerialCode,
                    null,
                    null,
                    null,
                    null,
                    0,
                    10
                )
            } returns pageTerminal.right()
            every { toPagedTerminalMapper.map(pageTerminal) } returns result

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isOk)

            verify(exactly = 1) { findTerminalByFilter.execute(aSerialCode, null, null, null, null, 0, 10) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("terminal NOT found with serial code") {

            val uri = "/private/terminals?serialCode=$aSerialCode"
            every { httpServletRequest.requestURI } returns uri

            val error = terminalNotFound(aSerialCode)

            every { findTerminalByFilter.execute(aSerialCode, null, null, null, null, 0, 10) } returns error.left()
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

            verify(exactly = 1) { findTerminalByFilter.execute(aSerialCode, null, null, null, null, 0, 10) }
            verify(exactly = 1) { errorProvider.provideFor(error) }
        }

        scenario("terminal found with customer id") {

            val pageTerminal = PageImpl(listOf(aTerminal))
            val result: PagedModel<TerminalModel> = PagedModel.empty()

            val uri = "/private/terminals?customerId=850363d9-8a9d-4843-acf1-1c1f09be86ab"
            every { httpServletRequest.requestURI } returns uri

            every {
                findTerminalByFilter.execute(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    0,
                    10
                )
            } returns pageTerminal.right()

            every { toPagedTerminalMapper.map(pageTerminal) } returns result

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isOk)

            verify(exactly = 1) {
                findTerminalByFilter.execute(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    0,
                    10
                )
            }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("terminal NOT found with customer id") {
            val uri = "/private/terminals?customerId=850363d9-8a9d-4843-acf1-1c1f09be86ab"
            every { httpServletRequest.requestURI } returns uri

            val error = terminalNotFound(UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"))

            every {
                findTerminalByFilter.execute(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    0,
                    10
                )
            } returns error.left()
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

            verify(exactly = 1) {
                findTerminalByFilter.execute(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    0,
                    10
                )
            }
            verify(exactly = 1) { errorProvider.provideFor(error) }
        }
    }

    feature("create new terminal") {
        val uri = "/private/terminals/"
        val terminalRequest = aTerminalRequest()

        scenario("with valid terminal") {
            val preTerminal = aPreTerminal()

            every { httpServletRequest.requestURI } returns "/private/terminals/"
            every { toPreTerminalMapper.map(terminalRequest) } returns preTerminal
            every {
                findTerminalPortIn.findByUnivocity(
                    preTerminal.serialCode,
                    preTerminal.tradeMark,
                    preTerminal.model
                )
            } returns Optional.empty()
            every { createTerminalPortIn.execute(preTerminal, Optional.empty()) } returns aTerminal.right()
            every { mapper.mapFrom(aTerminal) } returns aTerminalResponse

            mockMvc.perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule()).setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(terminalRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(aTerminalResponse.id.toString()))
                .andExpect(jsonPath("$.merchant_id").value(aTerminalResponse.merchantId.toString()))
                .andExpect(jsonPath("$.customer_id").value(aTerminalResponse.customerId.toString()))
                .andExpect(jsonPath("$.serial_code").value(aTerminalResponse.serialCode))
                .andExpect(jsonPath("$.hardware_version").value(aTerminalResponse.hardwareVersion))
                .andExpect(jsonPath("$.trade_mark").value(aTerminalResponse.tradeMark))
                .andExpect(jsonPath("$.model").value(aTerminalResponse.model))
                .andExpect(jsonPath("$.status").value(aTerminalResponse.status.name))
                .andExpect(jsonPath("$.features[0]").value(aTerminalResponse.features[0].name))
                .andExpect(jsonPath("$.features[1]").value(aTerminalResponse.features[1].name))
                .andExpect(jsonPath("$.features[2]").value(aTerminalResponse.features[2].name))
                .andExpect(jsonPath("$.features[3]").value(aTerminalResponse.features[3].name))

            verify(exactly = 1) { createTerminalPortIn.execute(preTerminal, Optional.empty()) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("with invalid feature") {
            every { httpServletRequest.requestURI } returns "/private/terminals/"

            mockMvc.perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper()
                            .registerModule(JavaTimeModule())
                            .setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(terminalRequest)
                            .replace(
                                "features", JSONArray().put("INVALID_FEATURE").toString()
                            )
                    )
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("012"))
                .andExpect(jsonPath("errors[0].resource").value("/private/terminals/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").isNotEmpty)
        }

        scenario("when terminal exists") {
            val preTerminal = aPreTerminal()
            val terminal = aTerminal

            every { httpServletRequest.requestURI } returns "/private/terminals/"
            every { toPreTerminalMapper.map(terminalRequest) } returns preTerminal
            every {
                findTerminalPortIn.findByUnivocity(
                    preTerminal.serialCode,
                    preTerminal.tradeMark,
                    preTerminal.model
                )
            } returns Optional.of(terminal)
            every {
                createTerminalPortIn.execute(
                    preTerminal,
                    Optional.of(terminal)
                )
            } returns ApplicationError.terminalExists().left()
            every { errorProvider.provideFor(ApplicationError.terminalExists()) } returns anApiErrorResponse

            mockMvc.perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule()).setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(terminalRequest)
                    )
            ).andExpect(status().isUnprocessableEntity)

            verify(exactly = 1) { createTerminalPortIn.execute(preTerminal, Optional.of(terminal)) }
            verify(exactly = 1) { toPreTerminalMapper.map(terminalRequest) }
            verify(exactly = 1) {
                findTerminalPortIn.findByUnivocity(
                    preTerminal.serialCode,
                    preTerminal.tradeMark,
                    preTerminal.model
                )
            }
            verify(exactly = 1) { errorProvider.provideFor(ApplicationError.terminalExists()) }
        }
    }

    feature("delete terminal by terminalId") {
        val terminalId = aTerminalId
        val uri = "/private/terminals/$terminalId"
        val terminal = aTerminal
        val terminalDelete = aTerminalDelete

        every { httpServletRequest.requestURI } returns uri

        scenario("with terminal NOT deleted") {
            every { findTerminalPortIn.execute(terminalId) } returns terminal.right()
            every { deleteTerminalPortIn.execute(terminal) } returns terminalDelete.right()

            mockMvc.perform(
                delete(uri)
            ).andExpect(status().isNoContent)

            verify(exactly = 1) { findTerminalPortIn.execute(terminalId) }
            verify(exactly = 1) { deleteTerminalPortIn.execute(terminal) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("terminal NOT found") {
            val error = terminalNotFound(aTerminalId)

            every { findTerminalPortIn.execute(aTerminalId) } returns error.left()
            every { errorProvider.provideFor(error) } returns anApiErrorResponse

            mockMvc.perform(
                delete(uri)
            ).andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(anApiErrorResponse.errors[0].code))
                .andExpect(jsonPath("errors[0].resource").value(anApiErrorResponse.errors[0].resource))
                .andExpect(jsonPath("errors[0].metadata.a_key").value(anApiErrorResponse.errors[0].metadata["a_key"]))
                .andExpect(jsonPath("errors[0].message").value(anApiErrorResponse.errors[0].message))

            verify(exactly = 1) { findTerminalPortIn.execute(aTerminalId) }
            verify(exactly = 1) { errorProvider.provideFor(error) }
            verify(exactly = 0) { deleteTerminalPortIn.execute(any()) }
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
