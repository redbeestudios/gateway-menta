package com.menta.api.feenicia.adapter.controller

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationMapper
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.feenicia.application.aCreatedOperation
import com.menta.api.feenicia.application.aFeeniciaMerchant
import com.menta.api.feenicia.application.aPaymentOperation
import com.menta.api.feenicia.application.anOperationRequest
import com.menta.api.feenicia.application.anOperationResponse
import com.menta.api.feenicia.application.port.`in`.CreateOperationInPort
import com.menta.api.feenicia.application.port.`in`.FindFeeniciaMerchantInPort
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

class PaymentControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toOperationResponseMapper: ToOperationResponseMapper = mockk()
    val toOperationMapper: ToOperationMapper = mockk()
    val createOperationInPort: CreateOperationInPort = mockk()
    val errorResponseProvider: ErrorResponseProvider = mockk()
    val findFeeniciaMerchantInPort: FindFeeniciaMerchantInPort = mockk()
    val controller =
        PaymentController(createOperationInPort, toOperationResponseMapper, toOperationMapper, errorResponseProvider, findFeeniciaMerchantInPort)

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/private/payments"
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

    feature("payment creation") {

        scenario("successful creation with installments") {
            val request = anOperationRequest()
            val operation = aPaymentOperation()
            val createdOperation = aCreatedOperation()
            val response = anOperationResponse()

            every { toOperationMapper.map(request, PAYMENT, aFeeniciaMerchant()) } returns operation
            every { createOperationInPort.execute(operation) } returns createdOperation.right()
            every { toOperationResponseMapper.map(createdOperation, request) } returns response
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/payments"
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(
                    jsonPath("$.authorization.code").value(response.authorization.code)
                )
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(
                    jsonPath("$.authorization.status.code")
                        .value(response.authorization.status?.code)
                )
                .andExpect(
                    jsonPath("$.authorization.status.situation.id")
                        .value(response.authorization.status?.situation?.id)
                )
                .andExpect(
                    jsonPath("$.authorization.status.situation.description")
                        .value(response.authorization.status?.situation?.description)
                )
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(
                    jsonPath("$.amount.breakdown[0].description")
                        .value(response.amount.breakdown[0].description)
                )
                .andExpect(
                    jsonPath("$.amount.breakdown[0].amount")
                        .value(response.amount.breakdown[0].amount)
                )
                .andExpect(jsonPath("$.installments").value(response.installments))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant?.id.toString()))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(
                    jsonPath("$.terminal.software_version")
                        .value(response.terminal.softwareVersion)
                )
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(
                    jsonPath("$.capture.card.holder.name")
                        .value(response.capture.card?.holder?.name)
                )
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.number")
                        .value(response.capture.card?.holder?.identification?.number)
                )
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.type")
                        .value(response.capture.card?.holder?.identification?.type)
                )
                .andExpect(
                    jsonPath("$.capture.card.masked_pan").value(response.capture.card?.maskedPan)
                )
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card?.brand))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card?.type))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card?.bank))
                .andExpect(
                    jsonPath("$.capture.previous_transaction_input_mode")
                        .value(response.capture.previousTransactionInputMode)
                )
                .andExpect(
                    jsonPath("$.capture.input_mode").value(response.capture.inputMode)
                )
                .andExpect(
                    jsonPath("$.datetime")
                        .value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME))
                )
        }

        scenario("unsuccessful creation") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/payments"
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})
