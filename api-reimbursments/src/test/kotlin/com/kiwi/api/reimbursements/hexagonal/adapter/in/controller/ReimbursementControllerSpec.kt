package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper.ToReimbursementDomainMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper.ToReimbursementResponseMapper
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedRefund
import com.kiwi.api.reimbursements.hexagonal.application.aCustomer
import com.kiwi.api.reimbursements.hexagonal.application.aMerchant
import com.kiwi.api.reimbursements.hexagonal.application.aRefund
import com.kiwi.api.reimbursements.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reimbursements.hexagonal.application.aReimbursementResponse
import com.kiwi.api.reimbursements.hexagonal.application.aTerminal
import com.kiwi.api.reimbursements.hexagonal.application.aTransaction
import com.kiwi.api.reimbursements.hexagonal.application.acquirerId
import com.kiwi.api.reimbursements.hexagonal.application.anAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.batch
import com.kiwi.api.reimbursements.hexagonal.application.breakdownAmount
import com.kiwi.api.reimbursements.hexagonal.application.breakdownDescription
import com.kiwi.api.reimbursements.hexagonal.application.cardBank
import com.kiwi.api.reimbursements.hexagonal.application.cardBrand
import com.kiwi.api.reimbursements.hexagonal.application.cardCVV
import com.kiwi.api.reimbursements.hexagonal.application.cardExpirationDate
import com.kiwi.api.reimbursements.hexagonal.application.cardIccData
import com.kiwi.api.reimbursements.hexagonal.application.cardPan
import com.kiwi.api.reimbursements.hexagonal.application.cardSequenceNumber
import com.kiwi.api.reimbursements.hexagonal.application.cardType
import com.kiwi.api.reimbursements.hexagonal.application.currency
import com.kiwi.api.reimbursements.hexagonal.application.datetime
import com.kiwi.api.reimbursements.hexagonal.application.features
import com.kiwi.api.reimbursements.hexagonal.application.holderName
import com.kiwi.api.reimbursements.hexagonal.application.identificationNumber
import com.kiwi.api.reimbursements.hexagonal.application.identificationType
import com.kiwi.api.reimbursements.hexagonal.application.inputMode
import com.kiwi.api.reimbursements.hexagonal.application.installments
import com.kiwi.api.reimbursements.hexagonal.application.ksn
import com.kiwi.api.reimbursements.hexagonal.application.merchantId
import com.kiwi.api.reimbursements.hexagonal.application.paymentId
import com.kiwi.api.reimbursements.hexagonal.application.pin
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTransactionPortIn
import com.kiwi.api.reimbursements.hexagonal.application.previousTransactionInputMode
import com.kiwi.api.reimbursements.hexagonal.application.softwareVersion
import com.kiwi.api.reimbursements.hexagonal.application.terminalId
import com.kiwi.api.reimbursements.hexagonal.application.terminalSerialCode
import com.kiwi.api.reimbursements.hexagonal.application.ticket
import com.kiwi.api.reimbursements.hexagonal.application.totalAmount
import com.kiwi.api.reimbursements.hexagonal.application.trace
import com.kiwi.api.reimbursements.hexagonal.application.track1
import com.kiwi.api.reimbursements.hexagonal.application.track2
import com.kiwi.api.reimbursements.shared.error.ErrorHandler
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.transactionNotFound
import com.kiwi.api.reimbursements.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.reimbursements.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.reimbursements.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.reimbursements.shared.error.providers.ErrorResponseProvider
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

class ReimbursementControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toReimbursementResponseMapper: ToReimbursementResponseMapper = mockk()
    val toReimbursementDomainMapper: ToReimbursementDomainMapper = mockk()
    val createAnnulmentPortIn: CreateAnnulmentPortIn = mockk()
    val createRefundPortIn: CreateRefundPortIn = mockk()
    val findCustomerPortIn: FindCustomerPortIn = mockk()
    val findTerminalPortIn: FindTerminalPortIn = mockk()
    val findMerchantPortIn: FindMerchantPortIn = mockk()
    val findTransactionPortIn: FindTransactionPortIn = mockk()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()
    val errorResponseProvider = ErrorResponseProvider(currentResourceProvider, metadataProvider)

    val controller = ReimbursementController(
        createAnnulmentPortIn,
        createRefundPortIn,
        findCustomerPortIn,
        findTerminalPortIn,
        findMerchantPortIn,
        findTransactionPortIn,
        toReimbursementResponseMapper,
        toReimbursementDomainMapper,
        errorResponseProvider
    )

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    val objectMapper = Jackson2ObjectMapperBuilder().propertyNamingStrategy(SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(aControllerAdvice(httpServletRequest))
        .setMessageConverters(objectMapper).build()

    feature("annulment creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/annulments"
        }

        scenario("successful creation") {

            val request = aReimbursementRequest()
            val annulment = anAnnulment()
            val createdAnnulment = aCreatedAnnulment()
            val response = aReimbursementResponse()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val transaction = aTransaction()

            every {
                toReimbursementDomainMapper.mapToAnnulment(
                    request,
                    terminal,
                    merchant,
                    customer
                )
            } returns annulment
            every { findTerminalPortIn.execute(request.terminal.id) } returns terminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { findTransactionPortIn.execute(request.paymentId) } returns transaction.right()
            every { createAnnulmentPortIn.execute(annulment) } returns createdAnnulment.right()
            every { toReimbursementResponseMapper.map(createdAnnulment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/annulments"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
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
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id.toString()))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id.toString()))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
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

        scenario("unsuccessful creation with invalid request") {
            val request = aReimbursementRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val transaction = aTransaction()

            every { findTerminalPortIn.execute(request.terminal.id) } returns terminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { findTransactionPortIn.execute(request.paymentId) } returns transaction.right()

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/annulments"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(anInvalidRequest())
            ).andExpect(status().isBadRequest).andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/annulments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }

        scenario("unsuccessful creation with transaction NOT FOUND") {
            val transactionId = paymentId

            val error = transactionNotFound(transactionId.toString())

            every { findTransactionPortIn.execute(transactionId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/public/annulments"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/annulments"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(aJsonRequest())
            ).andExpect(status().isNotFound).andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(404))
                .andExpect(jsonPath("errors[0].resource").value("/public/annulments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("refund creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/refunds"
        }

        scenario("successful creation") {

            val request = aReimbursementRequest()
            val refund = aRefund()
            val createdRefund = aCreatedRefund()
            val response = aReimbursementResponse()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val transaction = aTransaction()

            every { findTerminalPortIn.execute(request.terminal.id) } returns terminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { findTransactionPortIn.execute(request.paymentId) } returns transaction.right()
            every { toReimbursementDomainMapper.mapToRefund(request, terminal, merchant, customer) } returns refund
            every { createRefundPortIn.execute(refund) } returns createdRefund.right()
            every { toReimbursementResponseMapper.map(createdRefund) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/refunds"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
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
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id.toString()))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id.toString()))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
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

        scenario("unsuccessful creation with invalid request") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/refunds"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(anInvalidRequest())
            ).andExpect(status().isBadRequest).andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/refunds"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }

        scenario("unsuccessful creation with transaction NOT FOUND") {
            val transactionId = paymentId

            val error = transactionNotFound(transactionId.toString())

            every { findTransactionPortIn.execute(transactionId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/public/refunds"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/refunds"
                ).header("merchantId", merchantId).contentType(APPLICATION_JSON).content(aJsonRequest())
            ).andExpect(status().isNotFound).andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(404))
                .andExpect(jsonPath("errors[0].resource").value("/public/refunds"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aJsonRequest() = """
{
   "payment_id": "$paymentId",
   "acquirer_id": "$acquirerId",
   "capture": {
      "card": {
         "holder": {
            "name": "$holderName",
            "identification": {
               "number": "$identificationNumber",
               "type": "$identificationType"
            }
         },
         "pan": "$cardPan",
         "expiration_date": "$cardExpirationDate",
         "cvv": "$cardCVV",
         "bank": "$cardBank",
         "type": "$cardType",
         "brand": "$cardBrand",
         "icc_data": "$cardIccData",
         "card_sequence_number": "$cardSequenceNumber",
         "pin": "$pin",
         "ksn": "$ksn",
         "track1": "$track1",
         "track2": "$track2"
      },
      "input_mode": "$inputMode",
      "previous_transaction_input_mode": "$previousTransactionInputMode"
   },
   "amount": {
      "total": "$totalAmount",
      "currency": "$currency",
      "breakdown": [
         {
            "description": "$breakdownDescription",
            "amount": "$breakdownAmount"
         }
      ]
   },
   "datetime": "$datetime",
   "trace": "$trace",
   "ticket": "$ticket",
   "terminal": {
      "id": "$terminalId",
      "serial_code": "$terminalSerialCode",
      "software_version": "$softwareVersion",
      "features": [
        ${features.joinToString(", ") { "\"$it\"" }}
      ]
   },
   "batch": "$batch",
   "installments": "$installments"
}
""".trimIndent()

fun aRequestWithInvalidFeature() = aJsonRequest().replace("\"CHIP\", \"CONTACTLESS\"", "\"1\"")

fun anInvalidRequest() = aJsonRequest().replace("\"currency\": \"ARS\",", "")

fun aControllerAdvice(request: HttpServletRequest) = ErrorHandler(
    errorResponseProvider = ErrorResponseProvider(
        currentResourceProvider = CurrentResourceProvider(request),
        metadataProvider = ErrorResponseMetadataProvider(
            currentResourceProvider = CurrentResourceProvider(request)
        )
    )
)
