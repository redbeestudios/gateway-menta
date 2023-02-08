package com.menta.api.credibanco.adapter.controller

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.menta.api.credibanco.aControllerAdvice
import com.menta.api.credibanco.aCreatedOperation
import com.menta.api.credibanco.aCredibancoMerchant
import com.menta.api.credibanco.aCredibancoTerminal
import com.menta.api.credibanco.aPaymentResponse
import com.menta.api.credibanco.aRequestPayment
import com.menta.api.credibanco.adapter.controller.mapper.ToOperationMapper
import com.menta.api.credibanco.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.credibanco.anApiErrorResponse
import com.menta.api.credibanco.anOperation
import com.menta.api.credibanco.application.port.`in`.CreateOperationPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoMerchantPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoTerminalPortIn
import com.menta.api.credibanco.application.port.`in`.ValidateTerminalPortIn
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.merchantId
import com.menta.api.credibanco.shared.error.model.OutdatedTerminal
import com.menta.api.credibanco.shared.error.providers.ErrorResponseProvider
import com.menta.api.credibanco.terminalId
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

class PaymentControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val createOperationPortIn = mockk<CreateOperationPortIn>()
    val operationMapper = mockk<ToOperationMapper>()
    val toOperationResponseMapper = mockk<ToOperationResponseMapper>()
    val errorResponseProvider = mockk<ErrorResponseProvider>()
    val findCredibancoTerminalPortIn = mockk<FindCredibancoTerminalPortIn>()
    val findCredibancoMerchantPortIn = mockk<FindCredibancoMerchantPortIn>()
    val validateTerminal = mockk<ValidateTerminalPortIn>()

    val controller = PaymentController(
        createOperation = createOperationPortIn,
        toOperationMapper = operationMapper,
        toOperationResponseMapper = toOperationResponseMapper,
        errorResponseProvider = errorResponseProvider,
        findCredibancoTerminalPortIn = findCredibancoTerminalPortIn,
        findCredibancoMerchantPortIn = findCredibancoMerchantPortIn,
        validateTerminal = validateTerminal
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
        .serializationInclusion(NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(aControllerAdvice(httpServletRequest))
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/private/payments"
        every { httpServletRequest.queryString } returns null
    }

    feature("payment controller") {

        scenario("successful creation") {
            val request = aRequestPayment()
            val operation = anOperation
            val createdOperation = aCreatedOperation()
            val response = aPaymentResponse()
            val credibancoTerminal = aCredibancoTerminal()
            val credibancoMerchant = aCredibancoMerchant()

            every { validateTerminal.execute(request.terminal) } returns true.right()
            every { findCredibancoTerminalPortIn.execute(terminalId) } returns credibancoTerminal
            every { findCredibancoMerchantPortIn.execute(merchantId) } returns credibancoMerchant
            every {
                operationMapper.map(
                    request,
                    PURCHASE,
                    credibancoTerminal,
                    credibancoMerchant
                )
            } returns operation
            every { createOperationPortIn.execute(operation) } returns Either.Right(createdOperation)
            every { toOperationResponseMapper.map(createdOperation, request) } returns response

            mockMvc.perform(
                post(
                    "/private/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonPaymentRequest)
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
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
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id.toString()))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(
                    jsonPath("$.terminal.software_version")
                        .value(response.terminal.softwareVersion)
                )
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id.toString()))
                .andExpect(
                    jsonPath("$.capture.card.holder.name")
                        .value(response.capture.card.holder.name)
                )
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.number")
                        .value(response.capture.card.holder.identification?.number)
                )
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.type")
                        .value(response.capture.card.holder.identification?.type)
                )
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(
                    jsonPath("$.capture.previous_transaction_input_mode")
                        .value(response.capture.previousTransactionInputMode)
                )
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
        }

        scenario("unsuccessful terminal validation") {

            val request = aRequestPayment()
            val error = OutdatedTerminal(request.terminal.serialCode)

            every { validateTerminal.execute(request.terminal) } returns error.left()
            every { errorResponseProvider.provideFor(error) } returns anApiErrorResponse

            mockMvc.perform(
                post(
                    "/private/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonPaymentRequest)
            ).andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(anApiErrorResponse.errors[0].code))
                .andExpect(jsonPath("errors[0].resource").value(anApiErrorResponse.errors[0].resource))
                .andExpect(jsonPath("errors[0].metadata.a_key").value(anApiErrorResponse.errors[0].metadata["a_key"]))
                .andExpect(jsonPath("errors[0].message").value(anApiErrorResponse.errors[0].message))

            verify(exactly = 1) { validateTerminal.execute(request.terminal) }
            verify(exactly = 0) { findCredibancoTerminalPortIn.execute(terminalId) }
            verify(exactly = 0) { findCredibancoMerchantPortIn.execute(merchantId) }
            verify(exactly = 0) {
                operationMapper.map(
                    request,
                    PURCHASE,
                    aCredibancoTerminal(),
                    aCredibancoMerchant()
                )
            }
            verify(exactly = 0) { createOperationPortIn.execute(anOperation) }
            verify(exactly = 0) { toOperationResponseMapper.map(aCreatedOperation(), request) }
        }
    }
})

private val aJsonPaymentRequest = """
{
    "amount": {
        "breakdown": [
            {
                "amount": "200000",
                "description": "OPERATION"
            }
        ],
        "currency": "COP",
        "total": "200000"
    },
    "batch": "2",
    "capture": {
        "card": {
            "bank": "SANTANDER",
            "brand": "MASTERCARD",
            "expiration_date": "2512",
            "holder": {
                "identification": {
                    "number": "0951377878",
                    "type": "DNI"
                },
                "name": "MTIP06  MCD  15A"
            },
            "emv": {
                "icc_data": "CARD_ICC_DATA",
                "card_sequence_number": "123",
                "ksn": "123"
            },
            "pan": "5413330089020011",
            "cvv": "123",
            "pin": "123",
            "track1": "5413330089020011D2512601079360805",
            "track2": "5413330089020011D2512601079360805",
            "type": "CREDIT"
        },
        "input_mode": "STRIPE",
        "previous_transaction_input_mode": "CHIP"
    },
    "datetime": "2022-01-19T11:23:23Z",
    "installments": "01",
    "terminal": {
        "id": "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        "merchant_id": "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        "customer_id": "6f14d0ab-9605-4a62-a9e4-5ed26688389b",
        "serial_code": "03000021",
        "hardware_version": "abc123",
        "software_version": "10",
        "trade_mark": "pirutchit",
        "model": "zg300",
        "status": "ACTIVE",
        "features": [
            "CONTACTLESS"
        ]
    },
    "merchant": {
        "id": "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        "customer_id": "6f14d0ab-9605-4a62-a9e4-5ed26688389b",
        "country": "CO",
        "legal_type": "NATURAL_PERSON",
        "business_name": "Gerbers gin",
        "fantasy_name": "Menta",
        "business_owner":{
            "name": "Jose",
            "surname": "Perez",
            "birth_date": "2022-01-19T11:23:23Z",
            "id":{
                "type": "DNI",
                "number": "999999999"
            }
        },
         "representative":{
            "id": {
                "type": "type",
                "number": "number"
            },
            "birth_date":"2022-01-19T11:23:23Z",
            "name": "representativeName",
            "surname":"representativeSurname"
        },
        "merchant_code": "123",
        "address": {
            "state": "Antioquia",
            "city": "Medellin",
            "zip": "5001",
            "street": "Street1",
            "number": "123",
            "floor": "1",
            "apartment": "A"
        },
        "email": "hola@hola",
        "phone": "1111111111",
        "activity" : "a activity",
        "category" : "7372",
         "tax": {
            "id": "id",
            "type":"monotributista"
        },
        "settlement_condition": {
            "transaction_fee":"transactionFee",
            "settlement": "settlement",
            "cbu_or_cvu":"123123123123123"
        }
    },
    "ticket": "2",
    "trace": "000002",
    "retrieval_reference_number": null,
    "customer": {
        "id": "6f14d0ab-9605-4a62-a9e4-5ed26688389b",
        "country": "CO",
        "business_name": "name43543",
        "fantasy_name": "fantasyName1",
        "constitution_datetime": "2022-01-19T11:23:23Z",
        "tax": {
            "type": "1",
            "id": "2"
        },
        "activity": "Activity",
        "category": "Category",
        "address": {
            "state": "Antioquia",
            "city": "Medellin",
            "zip": "5001",
            "street": "Street1",
            "number": "125",
            "floor": "1",
            "apartment": "A"
        },
        "status": "ACTIVE"
    }
}
""".trimIndent()
