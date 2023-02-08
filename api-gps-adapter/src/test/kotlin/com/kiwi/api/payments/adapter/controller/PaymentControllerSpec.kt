package com.kiwi.api.payments.adapter.controller

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.kiwi.api.payments.adapter.controller.mapper.ToOperationMapper
import com.kiwi.api.payments.adapter.controller.mapper.ToOperationResponseMapper
import com.kiwi.api.payments.application.aCreatedOperation
import com.kiwi.api.payments.application.aPaymentResponse
import com.kiwi.api.payments.application.anAcquirerCustomer
import com.kiwi.api.payments.application.anAcquirerMerchant
import com.kiwi.api.payments.application.anAcquirerTerminal
import com.kiwi.api.payments.application.anOperation
import com.kiwi.api.payments.application.anRequestPayment
import com.kiwi.api.payments.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerCustomerInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerMerchantInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerTerminalInPort
import com.kiwi.api.payments.domain.OperationType.PURCHASE
import com.kiwi.api.payments.shared.error.ErrorHandler
import com.kiwi.api.payments.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.payments.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
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
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class PaymentControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toOperationResponseMapper: ToOperationResponseMapper = mockk()
    val toOperationMapper: ToOperationMapper = mockk()
    val createPaymentPortIn: CreateOperationInPort = mockk()
    val errorResponseProvider: ErrorResponseProvider = mockk()
    val findAcquirerCustomerInPort: FindAcquirerCustomerInPort = mockk()
    val findAcquirerMerchantInPort: FindAcquirerMerchantInPort = mockk()
    val findAcquirerTerminalInPort: FindAcquirerTerminalInPort = mockk()
    val merchantId = "1234"
    val controller = PaymentController(
        createPaymentPortIn,
        toOperationResponseMapper,
        toOperationMapper,
        errorResponseProvider,
        findAcquirerCustomerInPort,
        findAcquirerMerchantInPort,
        findAcquirerTerminalInPort
    )

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/private/payments"
        every { httpServletRequest.queryString } returns null
    }

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
        .build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvicePayment(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    feature("Payment controller") {

        scenario("successful creation") {
            val request = anRequestPayment()
            val payment = anOperation
            val createdPayment = aCreatedOperation()
            val response = aPaymentResponse()
            val responseAcquirer = anAcquirerCustomer()
            val responseTerminal = anAcquirerTerminal()
            val responseMerchant = anAcquirerMerchant()

            every { findAcquirerCustomerInPort.execute(UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b")) } returns responseAcquirer
            every { findAcquirerMerchantInPort.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns responseMerchant
            every { findAcquirerTerminalInPort.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns responseTerminal
            every { toOperationMapper.map(request, PURCHASE, responseAcquirer, responseMerchant, responseTerminal) } returns payment
            every { createPaymentPortIn.execute(payment) } returns Either.Right(createdPayment)
            every { toOperationResponseMapper.map(createdPayment, request) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/payments"
                )
                    .header("merchantId", merchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aJsonPaymentRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.retrieval_reference_number").value(response.authorization.retrievalReferenceNumber))
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(jsonPath("$.amount.breakdown[0].description").value(response.amount.breakdown[0].description))
                .andExpect(jsonPath("$.amount.breakdown[0].amount").value(response.amount.breakdown[0].amount))
                .andExpect(jsonPath("$.installments").value(response.installments))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id.toString()))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id.toString()))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(jsonPath("$.capture.card.holder.identification.number").value(response.capture.card.holder.identification?.number))
                .andExpect(jsonPath("$.capture.card.holder.identification.type").value(response.capture.card.holder.identification?.type))
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(jsonPath("$.capture.previous_transaction_input_mode").value(response.capture.previousTransactionInputMode))
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
        }

        scenario("unsuccessful creation") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/payments"
                )
                    .header("merchantId", merchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(anInvalidRequestPayment())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aJsonPaymentRequest() = """
{
    "amount": {
        "breakdown": [
            {
                "amount": "2000",
                "description": "OPERATION"
            }
        ],
        "currency": "ARS",
        "total": "2000"
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
        "input_mode": "EMV",
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
        "country": "ARG",
        "legal_type": "NATURAL_PERSON",
        "business_name": "Kiwi",
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
            "state": "Argentina",
            "city": "CABA",
            "zip": "123",
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
    "retrieval_reference_number": "111111111111",
    "customer": {
        "id": "6f14d0ab-9605-4a62-a9e4-5ed26688389b",
        "country": "ARG",
        "business_name": "name43543",
        "fantasy_name": "fantasyName1",
        "constitution_datetime": "2022-01-19T11:23:23Z",
        "tax": {
            "type": "1",
            "id": "2"
        },
        "activity": "Activity",
        "address": {
            "state": "Argentina",
            "city": "CABA",
            "zip": "123",
            "street": "Street1",
            "number": "123",
            "floor": "1",
            "apartment": "A"
        },
        "status": "ACTIVE"
    }
}
""".trimIndent()

fun anInvalidRequestPayment() = aJsonPaymentRequest().replace("\"currency\": \"ARS\",", "")

fun aControllerAdvicePayment(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )
