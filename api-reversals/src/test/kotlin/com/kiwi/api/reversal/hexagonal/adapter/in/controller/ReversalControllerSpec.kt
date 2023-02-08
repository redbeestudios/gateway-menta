package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper.ToDomainMapper
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper.ToResponseMapper
import com.kiwi.api.reversal.hexagonal.adapter.out.event.AnnulmentReversalProducer
import com.kiwi.api.reversal.hexagonal.adapter.out.event.PaymentReversalProducer
import com.kiwi.api.reversal.hexagonal.adapter.out.event.RefundReversalProducer
import com.kiwi.api.reversal.hexagonal.application.aBatchClose
import com.kiwi.api.reversal.hexagonal.application.aBatchCloseRequest
import com.kiwi.api.reversal.hexagonal.application.aBatchCloseResponse
import com.kiwi.api.reversal.hexagonal.application.aCustomer
import com.kiwi.api.reversal.hexagonal.application.aMerchant
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.aPaymentRequest
import com.kiwi.api.reversal.hexagonal.application.aPaymentResponse
import com.kiwi.api.reversal.hexagonal.application.aReceivedTerminal
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reversal.hexagonal.application.aReimbursementResponse
import com.kiwi.api.reversal.hexagonal.application.aTerminal
import com.kiwi.api.reversal.hexagonal.application.acquirerId
import com.kiwi.api.reversal.hexagonal.application.anAnnulment
import com.kiwi.api.reversal.hexagonal.application.batch
import com.kiwi.api.reversal.hexagonal.application.batchCloseTotals
import com.kiwi.api.reversal.hexagonal.application.breakdownAmount
import com.kiwi.api.reversal.hexagonal.application.breakdownDescription
import com.kiwi.api.reversal.hexagonal.application.cardBank
import com.kiwi.api.reversal.hexagonal.application.cardBrand
import com.kiwi.api.reversal.hexagonal.application.cardCVV
import com.kiwi.api.reversal.hexagonal.application.cardExpirationDate
import com.kiwi.api.reversal.hexagonal.application.cardIccData
import com.kiwi.api.reversal.hexagonal.application.cardInputMode
import com.kiwi.api.reversal.hexagonal.application.cardPan
import com.kiwi.api.reversal.hexagonal.application.cardSequenceNumber
import com.kiwi.api.reversal.hexagonal.application.cardType
import com.kiwi.api.reversal.hexagonal.application.currency
import com.kiwi.api.reversal.hexagonal.application.holderName
import com.kiwi.api.reversal.hexagonal.application.hostMessage
import com.kiwi.api.reversal.hexagonal.application.identificationNumber
import com.kiwi.api.reversal.hexagonal.application.identificationType
import com.kiwi.api.reversal.hexagonal.application.installments
import com.kiwi.api.reversal.hexagonal.application.ksn
import com.kiwi.api.reversal.hexagonal.application.operationId
import com.kiwi.api.reversal.hexagonal.application.pin
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reversal.hexagonal.application.previousTransactionInputMode
import com.kiwi.api.reversal.hexagonal.application.softwareVersion
import com.kiwi.api.reversal.hexagonal.application.terminalId
import com.kiwi.api.reversal.hexagonal.application.ticket
import com.kiwi.api.reversal.hexagonal.application.totalAmount
import com.kiwi.api.reversal.hexagonal.application.trace
import com.kiwi.api.reversal.hexagonal.application.track1
import com.kiwi.api.reversal.hexagonal.application.track2
import com.kiwi.api.reversal.shared.error.ErrorHandler
import com.kiwi.api.reversal.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.reversal.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.reversal.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.reversal.shared.error.providers.ErrorResponseProvider
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

class ReversalControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toResponseMapper: ToResponseMapper = mockk()
    val toDomainMapper: ToDomainMapper = mockk()
    val createBatchClosePortIn: CreateBatchClosePortIn = mockk()
    val findCustomerPortIn: FindCustomerPortIn = mockk()
    val findTerminalPortIn: FindTerminalPortIn = mockk()
    val findMerchantPortIn: FindMerchantPortIn = mockk()
    val paymentReversalProducer: PaymentReversalProducer = mockk()
    val refundReversalProducer: RefundReversalProducer = mockk()
    val annulmentReversalProducer: AnnulmentReversalProducer = mockk()
    val errorResponseProvider: ErrorResponseProvider = mockk()

    val controller = ReversalController(
        createBatchClosePortIn,
        errorResponseProvider,
        toResponseMapper,
        toDomainMapper,
        findCustomerPortIn,
        findTerminalPortIn,
        findMerchantPortIn,
        paymentReversalProducer,
        annulmentReversalProducer,
        refundReversalProducer
    )

    beforeEach {
        clearAllMocks()
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

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reversals/payments"
        }

        scenario("successful creation") {
            val request = aPaymentRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()
            val payment = aPayment()
            val response = aPaymentResponse()

            every { findTerminalPortIn.execute(request.terminal.id) } returns receivedTerminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { toDomainMapper.mapToPayment(request, merchant, customer, receivedTerminal) } returns payment
            every { paymentReversalProducer.produce(payment) } returns Unit.right()
            every { toResponseMapper.map(payment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reversals/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.operation_id").value(response.operationId))
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
                    "/public/reversals/payments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reversals/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("annulment creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reversals/annulments"
        }

        scenario("successful creation") {
            val request = aReimbursementRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()
            val annulment = anAnnulment()
            val response = aReimbursementResponse()

            every { findTerminalPortIn.execute(request.terminal.id) } returns receivedTerminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { toDomainMapper.mapToAnnulment(request, merchant, customer, receivedTerminal) } returns annulment
            every { annulmentReversalProducer.produce(annulment) } returns Unit.right()
            every { toResponseMapper.map(annulment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reversals/annulments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.operation_id").value(response.operationId))
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
                    "/public/reversals/annulments"
                )
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reversals/annulments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("refund creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reversals/refunds"
        }

        scenario("successful creation") {
            val request = aReimbursementRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()
            val refund = aRefund()
            val response = aReimbursementResponse()

            every { findTerminalPortIn.execute(request.terminal.id) } returns receivedTerminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { toDomainMapper.mapToRefund(request, merchant, customer, receivedTerminal) } returns refund
            every { refundReversalProducer.produce(refund) } returns Unit.right()
            every { toResponseMapper.map(refund) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reversals/refunds"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.operation_id").value(response.operationId))
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
                    "/public/reversals/refunds"
                )
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reversals/refunds"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("batch close creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reversals/batch-closes"
        }

        scenario("successful creation") {
            val request = aBatchCloseRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()
            val batchClose = aBatchClose()
            val response = aBatchCloseResponse()

            every { findTerminalPortIn.execute(request.terminal.id) } returns receivedTerminal
            every { findCustomerPortIn.execute(terminal.customerId) } returns customer
            every { findMerchantPortIn.execute(terminal.merchantId) } returns merchant
            every { toDomainMapper.mapToBatchClose(request, merchant, customer, receivedTerminal) } returns batchClose
            every { createBatchClosePortIn.execute(batchClose) } returns batchClose
            every { toResponseMapper.map(batchClose) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reversals/batch-closes"
                )
                    .contentType(APPLICATION_JSON)
                    .content(aBatchCloseRequestJson())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.batch").value(response.batch))
                .andExpect(jsonPath("$.host_message").value(response.hostMessage))
                .andExpect(jsonPath("$.totals[0].operation_code").value(response.totals[0].operationCode.name))
                .andExpect(jsonPath("$.totals[0].amount").value(response.totals[0].amount))
                .andExpect(jsonPath("$.totals[0].currency").value(response.totals[0].currency))
                .andExpect(jsonPath("$.totals[1].operation_code").value(response.totals[1].operationCode.name))
                .andExpect(jsonPath("$.totals[1].amount").value(response.totals[1].amount))
                .andExpect(jsonPath("$.totals[1].currency").value(response.totals[1].currency))
                .andExpect(jsonPath("$.totals[2].operation_code").value(response.totals[2].operationCode.name))
                .andExpect(jsonPath("$.totals[2].amount").value(response.totals[2].amount))
                .andExpect(jsonPath("$.totals[2].currency").value(response.totals[2].currency))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
        }

        scenario("unsuccessful creation") {
            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reversals/batch-closes"
                )
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reversals/batch-closes"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aBatchCloseRequestJson() = """
    {
    	"datetime": "2022-01-19T11:23:23Z",
        "trace": "$trace",
        "ticket": "$ticket",
        "batch": "$batch",
        "terminal": {
            "id": "$terminalId",
            "software_version": "$softwareVersion"
        },
        "totals": [ ${
batchCloseTotals.joinToString(",") {
    """
            {
                "operation_code": "${it.operationCode}",
                "amount": "${it.amount}",
                "currency": "${it.currency}"
            }"""
}
}
        ]
    }
""".trimIndent()

fun aJsonRequest() = """
    {
       "operation_id": "$operationId",
       "acquirer_id": "$acquirerId",
       "capture": {
          "card": {
             "bank": "$cardBank",
             "type": "$cardType",
             "brand": "$cardBrand",
             "card_sequence_number": "$cardSequenceNumber",
             "icc_data": "$cardIccData",
             "pin": "$pin",
             "ksn": "$ksn",
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
             "track1": "$track1",
             "track2": "$track2"
          },
          "input_mode": "$cardInputMode",
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
       "datetime": "2022-01-19T11:23:23Z",
       "trace": "$trace",
       "ticket": "$ticket",
       "terminal": {
          "id": "$terminalId",
          "software_version": "$softwareVersion"
       },
       "batch": "$batch",
       "installments": "$installments",
       "host_message": "$hostMessage"
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
