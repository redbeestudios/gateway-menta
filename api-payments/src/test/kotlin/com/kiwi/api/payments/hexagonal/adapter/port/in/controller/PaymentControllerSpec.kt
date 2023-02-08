package com.kiwi.api.payments.hexagonal.adapter.port.`in`.controller

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.PaymentController
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentResponseMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.provider.PaymentProvider
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.aPaymentRequest
import com.kiwi.api.payments.hexagonal.application.aPaymentResponse
import com.kiwi.api.payments.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.payments.shared.error.ErrorHandler
import com.kiwi.api.payments.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.payments.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.payments.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType.APPLICATION_JSON
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
    val toPaymentResponseMapper: ToPaymentResponseMapper = mockk()
    val paymentProvider: PaymentProvider = mockk()
    val createPaymentPortIn: CreatePaymentPortIn = mockk()
    val controller = PaymentController(
        createPaymentPortIn, toPaymentResponseMapper, paymentProvider
    )

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/public/payments"
        every { httpServletRequest.queryString } returns null
    }

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
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

        scenario("successful creation") {
            val request = aPaymentRequest()
            val payment = aPayment()
            val eitherRight = aCreatedPayment().right()
            val createdPayment = aCreatedPayment()
            val response = aPaymentResponse()

            every { paymentProvider.provide(request) } returns payment
            every { createPaymentPortIn.execute(payment) } returns eitherRight
            every { toPaymentResponseMapper.map(createdPayment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id.toString()))
                .andExpect(jsonPath("$.status.code").value(response.status.code.name))
                .andExpect(jsonPath("$.status.situation.id").value(response.status.situation?.id))
                .andExpect(jsonPath("$.status.situation.description").value(response.status.situation?.description))
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.display_message").value(response.authorization.displayMessage))
                .andExpect(jsonPath("$.authorization.retrieval_reference_number").value(response.authorization.retrievalReferenceNumber))
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(jsonPath("$.amount.breakdown[0].description").value(response.amount.breakdown[0].description))
                .andExpect(jsonPath("$.amount.breakdown[0].amount").value(response.amount.breakdown[0].amount))
                .andExpect(jsonPath("$.installments").value(response.installments))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(jsonPath("$.capture.card.holder.identification.number").value(response.capture.card.holder.identification?.number))
                .andExpect(jsonPath("$.capture.card.holder.identification.type").value(response.capture.card.holder.identification?.type))
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(jsonPath("$.capture.previous_transaction_input_mode").value(response.capture.previousTransactionInputMode))
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode.name))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
        }

        scenario("unsuccessful creation") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aJsonRequest() = """
{
   "capture": {
      "card": {
         "holder": {
            "name": "sebastian",
            "identification": {
               "number": "444444",
               "type": "DNI"
            }
         },
         "pan": "333344445555",
         "expiration_date": "0622",
         "cvv": "234",
         "icc_data": "data",
         "brand": "VISA",
         "type": "DEBIT",
         "bank": "SANTANDER",
         "card_sequence_number": "card sequence",
         "track1": "track 1",
         "track2": "track2",
         "pin": "000",
         "ksn": "456"
      },
      "input_mode": "MANUAL"
   },
   "amount": {
      "total": "10000",
      "currency": "ARS",
      "breakdown": [
         {
            "description": "descripcion",
            "amount": "1000"
         }
      ]
   },
   "datetime": "2022-01-19T11:23:23Z",
   "trace": "123",
   "ticket": "234",
   "terminal": {
      "id": "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
      "software_version": "2.0"
   },
   "batch": "111",
   "installments": "10"
}
""".trimIndent()

fun anInvalidRequest() = aJsonRequest().replace("\"currency\": \"ARS\",", "")

fun aControllerAdvice(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )
