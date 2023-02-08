package com.menta.api.feenicia.adapter.controller

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationMapper
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.feenicia.application.aCreatedOperation
import com.menta.api.feenicia.application.aFeeniciaMerchant
import com.menta.api.feenicia.application.aRefundOperation
import com.menta.api.feenicia.application.anOperationRequest
import com.menta.api.feenicia.application.anOperationResponse
import com.menta.api.feenicia.application.port.`in`.CreateOperationInPort
import com.menta.api.feenicia.application.port.`in`.FindFeeniciaMerchantInPort
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

class ReimbursementControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toOperationResponseMapper: ToOperationResponseMapper = mockk()
    val toOperationMapper: ToOperationMapper = mockk()
    val createOperationInPort: CreateOperationInPort = mockk()
    val errorResponseProvider: ErrorResponseProvider = mockk()
    val findFeeniciaMerchantInPort: FindFeeniciaMerchantInPort = mockk()
    val controller =
        RefundController(createOperationInPort, toOperationResponseMapper, toOperationMapper, errorResponseProvider, findFeeniciaMerchantInPort)

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/private/refunds"
        every { httpServletRequest.queryString } returns null
        every { findFeeniciaMerchantInPort.execute(any()) } returns aFeeniciaMerchant()
    }

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
        .build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    feature("refund creation") {

        scenario("successful creation") {
            val request = anOperationRequest()
            val operation = aRefundOperation()
            val createdOperation = aCreatedOperation()
            val response = anOperationResponse()

            every { toOperationMapper.map(request, REFUND, aFeeniciaMerchant()) } returns operation
            every { createOperationInPort.execute(operation) } returns createdOperation.right()
            every { toOperationResponseMapper.map(createdOperation, request) } returns response
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/refunds"
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.authorization.status.code")
                        .value(response.authorization.status?.code)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.authorization.status.situation.id")
                        .value(response.authorization.status?.situation?.id)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.authorization.status.situation.description")
                        .value(response.authorization.status?.situation?.description)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.amount.breakdown[0].description")
                        .value(response.amount.breakdown[0].description)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.amount.breakdown[0].amount")
                        .value(response.amount.breakdown[0].amount)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.installments").value(response.installments))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trace").value(response.trace))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ticket").value(response.ticket))
                .andExpect(MockMvcResultMatchers.jsonPath("$.merchant.id").value(response.merchant?.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.terminal.software_version")
                        .value(response.terminal.softwareVersion)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.card.holder.name")
                        .value(response.capture.card?.holder?.name)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.card.holder.identification.number")
                        .value(response.capture.card?.holder?.identification?.number)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.card.holder.identification.type")
                        .value(response.capture.card?.holder?.identification?.type)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.card.masked_pan").value(response.capture.card?.maskedPan)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.capture.card.brand").value(response.capture.card?.brand))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capture.card.type").value(response.capture.card?.type))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capture.card.bank").value(response.capture.card?.bank))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.previous_transaction_input_mode")
                        .value(response.capture.previousTransactionInputMode)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.capture.input_mode").value(response.capture.inputMode)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.datetime")
                        .value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME))
                )
        }

        scenario("unsuccessful creation") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/refunds"
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("datetime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].resource").value("/private/refunds"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").exists())
        }
    }
})
