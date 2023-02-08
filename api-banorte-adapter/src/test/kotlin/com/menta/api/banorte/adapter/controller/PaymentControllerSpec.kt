package com.menta.api.banorte.adapter.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.banorte.adapter.controller.mapper.ToOperationMapper
import com.menta.api.banorte.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.banorte.application.aCreatedOperation
import com.menta.api.banorte.application.aMerchant
import com.menta.api.banorte.application.anOperation
import com.menta.api.banorte.application.anOperationRequest
import com.menta.api.banorte.application.anOperationResponse
import com.menta.api.banorte.application.port.`in`.CreateOperationInPort
import com.menta.api.banorte.application.port.`in`.FindBanorteMerchantInPort
import com.menta.api.banorte.domain.CommandTransaction.AUTH
import com.menta.api.banorte.shared.error.ErrorHandler
import com.menta.api.banorte.shared.error.model.MessageNotReadable
import com.menta.api.banorte.shared.error.model.ServerError
import com.menta.api.banorte.shared.error.providers.CurrentResourceProvider
import com.menta.api.banorte.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.banorte.shared.error.providers.ErrorResponseProvider
import com.menta.api.banorte.utils.TestConstants.Companion.AMOUNT
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_BANK
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_BRAND
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_CVV
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_EXPIRATION_DATE
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_ICC_DATA
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_KSN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_PAN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_PIN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TRACK1
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TRACK2
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TYPE
import com.menta.api.banorte.utils.TestConstants.Companion.CURRENCY
import com.menta.api.banorte.utils.TestConstants.Companion.DATE_TIME
import com.menta.api.banorte.utils.TestConstants.Companion.DESCRIPTION_BREAKDOWN
import com.menta.api.banorte.utils.TestConstants.Companion.HOLDER_NAME
import com.menta.api.banorte.utils.TestConstants.Companion.INPUT_MODE
import com.menta.api.banorte.utils.TestConstants.Companion.INSTALLMENTS
import com.menta.api.banorte.utils.TestConstants.Companion.MERCHANT_ID
import com.menta.api.banorte.utils.TestConstants.Companion.RRN
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_ID
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_SERIAL_CODE
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_SOFTWARE_VERSION
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_BATCH
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_TICKET
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_TRACE
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
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
    val toOperationResponseMapper: ToOperationResponseMapper = mockk()
    val toOperationMapper: ToOperationMapper = mockk()
    val findBanorteMerchantPortIn = mockk<FindBanorteMerchantInPort>()
    val createOperationInPort: CreateOperationInPort = mockk()
    val errorResponseProvider: ErrorResponseProvider = mockk()

    val controller = PaymentController(
        createOperationInPort,
        findBanorteMerchantPortIn,
        toOperationResponseMapper,
        toOperationMapper,
        errorResponseProvider
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
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    feature("payment creation") {
        val merchant = aMerchant()
        val request = anOperationRequest()
        val operation = anOperation()
        val createdOperation = aCreatedOperation()
        val response = anOperationResponse()

        scenario("successful creation") {
            every { findBanorteMerchantPortIn.execute(operation.merchantId) } returns merchant.right()
            every { toOperationMapper.map(request, merchant, AUTH) } returns operation
            every { createOperationInPort.execute(operation) } returns createdOperation.right()
            every { toOperationResponseMapper.map(createdOperation, request) } returns response

            mockMvc.perform(
                post("/private/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(
                    jsonPath("$.authorization.status.code")
                        .value(response.authorization.status.code)
                )
                .andExpect(
                    jsonPath("$.authorization.status.situation.id")
                        .value(response.authorization.status.situation.id)
                )
                .andExpect(
                    jsonPath("$.authorization.status.situation.description")
                        .value(response.authorization.status.situation.description)
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
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(
                    jsonPath("$.terminal.software_version")
                        .value(response.terminal.softwareVersion)
                )
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(
                    jsonPath("$.capture.card.holder.name")
                        .value(response.capture.card.holder.name)
                )
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.number")
                        .value(response.capture.card.holder.identification?.number)
                )
                .andExpect(
                    jsonPath("$.capture.card.holder.identification.type")
                        .value(response.capture.card.holder.identification?.type)
                )
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand.name))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(
                    jsonPath("$.capture.previous_transaction_input_mode")
                        .value(response.capture.previousTransactionInputMode)
                )
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))

            verify(exactly = 1) { findBanorteMerchantPortIn.execute(operation.merchantId) }
            verify(exactly = 1) { toOperationMapper.map(request, merchant, AUTH) }
            verify(exactly = 1) { createOperationInPort.execute(operation) }
            verify(exactly = 1) { toOperationResponseMapper.map(createdOperation, request) }
        }
        scenario("unsuccessful creation") {
            mockMvc.perform(
                post("/private/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 0) { findBanorteMerchantPortIn.execute(operation.merchantId) }
            verify(exactly = 0) { toOperationMapper.map(request, merchant, AUTH) }
            verify(exactly = 0) { createOperationInPort.execute(operation) }
            verify(exactly = 0) { toOperationResponseMapper.map(createdOperation, request) }
        }
        scenario("unsuccessful find banorte merchant") {
            every { findBanorteMerchantPortIn.execute(operation.merchantId) } returns ServerError().left()

            mockMvc.perform(
                post("/private/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isInternalServerError)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findBanorteMerchantPortIn.execute(operation.merchantId) }
            verify(exactly = 0) { toOperationMapper.map(request, merchant, AUTH) }
            verify(exactly = 0) { createOperationInPort.execute(operation) }
            verify(exactly = 0) { toOperationResponseMapper.map(createdOperation, request) }
        }
        scenario("unsuccessful: Invalid Card Brand") {
            mockMvc.perform(
                post("/private/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(aRequestWithInvalidCardBrand())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MessageNotReadable().code))
                .andExpect(jsonPath("errors[0].resource").value("/private/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))

            verify(exactly = 0) { findBanorteMerchantPortIn.execute(operation.merchantId) }
            verify(exactly = 0) { toOperationMapper.map(request, merchant, AUTH) }
            verify(exactly = 0) { createOperationInPort.execute(operation) }
            verify(exactly = 0) { toOperationResponseMapper.map(createdOperation, request) }
        }
    }
})

fun aJsonRequest() = """
{
   "capture": {
      "card": {
         "holder": {
            "name": "$HOLDER_NAME"
         },
         "pan": "$CARD_PAN",
         "expiration_date": "$CARD_EXPIRATION_DATE",
         "cvv": "$CARD_CVV",
         "brand": "$CARD_BRAND",
         "type": "$CARD_TYPE",
         "bank": "$CARD_BANK",
         "track1": "$CARD_TRACK1",
         "track2": "$CARD_TRACK2",
         "pin": "$CARD_PIN",
         "emv": {
              "ksn": "$CARD_KSN",
              "icc_data": "$CARD_ICC_DATA"
         }
      },
      "input_mode": "$INPUT_MODE"
   },
   "amount": {
      "total": "$AMOUNT",
      "currency": "$CURRENCY",
      "breakdown": [
         {
            "description": "$DESCRIPTION_BREAKDOWN",
            "amount": "$AMOUNT"
         }
      ]
   },
   "datetime": "$DATE_TIME",
   "trace": "$TRANSACTION_TRACE",
   "ticket": "$TRANSACTION_TICKET",
   "terminal": {
      "id": "$TERMINAL_ID",
      "serial_code": "$TERMINAL_SERIAL_CODE",
      "software_version": "$TERMINAL_SOFTWARE_VERSION",
      "features": [
         "CHIP"
      ]
   },
   "merchant": {
       "id": "$MERCHANT_ID"
   },
   "batch": "$TRANSACTION_BATCH",
   "installments": "$INSTALLMENTS",
   "retrieval_reference_number": "$RRN"
}
""".trimIndent()

fun aRequestWithInvalidCardBrand() = aJsonRequest().replace("\"brand\": \"$CARD_BRAND\",", "\"brand\": \"5\",")

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
