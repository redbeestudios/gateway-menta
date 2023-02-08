package com.kiwi.api.reverse.hexagonal.adapter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.kiwi.api.reverse.hexagonal.adapter.controller.mapper.ResponseMapper
import com.kiwi.api.reverse.hexagonal.application.*
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reverse.shared.error.ErrorHandler
import com.kiwi.api.reverse.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.reverse.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.reverse.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.reverse.shared.error.providers.ErrorResponseProvider
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

class ReverseControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val mapper: ResponseMapper = mockk()
    val createAnnulmentPortIn: CreateAnnulmentPortIn = mockk()
    val createRefundPortIn: CreateRefundPortIn = mockk()
    val createPaymentPortIn: CreatePaymentPortIn = mockk()
    val createBatchClosePortIn: CreateBatchClosePortIn = mockk()

    val controller = ReverseController(createAnnulmentPortIn, createRefundPortIn, createPaymentPortIn, createBatchClosePortIn, mapper)

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
            every { httpServletRequest.requestURI } returns "/public/reverse/payments"
        }

        scenario("successful creation") {

            val request = aPaymentRequest()
            val payment = aPayment()
            val response = aPaymentResponse()
            val merchantId = "merchantId"

            every { createPaymentPortIn.execute(request, merchantId) } returns payment
            every { mapper.map(payment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/payments"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.payment_id").value(response.paymentId))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.display_message").value(response.authorization.displayMessage))
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(jsonPath("$.amount.breakdown[0].description").value(response.amount.breakdown[0].description))
                .andExpect(jsonPath("$.amount.breakdown[0].amount").value(response.amount.breakdown[0].amount))
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(jsonPath("$.installments").value(response.installments))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.batch").value(response.batch))
                .andExpect(jsonPath("$.host_message").value(response.hostMessage))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
                .andExpect(jsonPath("$.capture.card.holder.identification.number").value(response.capture.card.holder.identification.number))
                .andExpect(jsonPath("$.capture.card.holder.identification.type").value(response.capture.card.holder.identification.type))
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(jsonPath("$.capture.card.card_sequence_number").value(response.capture.card.cardSequenceNumber))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
        }

        scenario("unsuccessful creation") {

            val request = aPaymentRequest()
            val payment = aPayment()
            val response = aPaymentResponse()
            val merchantId = "merchantId"

            every { createPaymentPortIn.execute(request, merchantId) } returns payment
            every { mapper.map(payment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/payments"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reverse/payments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("annulment creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reverse/annulments"
        }

        scenario("successful creation") {

            val request = aReimbursementRequest()
            val annulment = anAnnulment()
            val response = aReimbursementResponse()
            val merchantId = "merchantId"

            every { createAnnulmentPortIn.execute(request, merchantId) } returns annulment
            every { mapper.map(annulment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/annulments"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.payment_id").value(response.paymentId))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.display_message").value(response.authorization.displayMessage))
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(jsonPath("$.amount.breakdown[0].description").value(response.amount.breakdown[0].description))
                .andExpect(jsonPath("$.amount.breakdown[0].amount").value(response.amount.breakdown[0].amount))
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.batch").value(response.batch))
                .andExpect(jsonPath("$.host_message").value(response.hostMessage))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
                .andExpect(jsonPath("$.capture.card.holder.identification.number").value(response.capture.card.holder.identification.number))
                .andExpect(jsonPath("$.capture.card.holder.identification.type").value(response.capture.card.holder.identification.type))
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(jsonPath("$.capture.card.card_sequence_number").value(response.capture.card.cardSequenceNumber))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
        }

        scenario("unsuccessful creation") {

            val request = aReimbursementRequest()
            val annulment = anAnnulment()
            val response = aReimbursementResponse()
            val merchantId = ""

            every { createAnnulmentPortIn.execute(request, merchantId) } returns annulment
            every { mapper.map(annulment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/annulments"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reverse/annulments"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("refund creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reverse/refunds"
        }

        scenario("successful creation") {

            val request = aReimbursementRequest()
            val refund = aRefund()
            val response = aReimbursementResponse()
            val merchantId = "merchantId"

            every { createRefundPortIn.execute(request, merchantId) } returns refund
            every { mapper.map(refund) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/refunds"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(aJsonRequest())
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.payment_id").value(response.paymentId))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.display_message").value(response.authorization.displayMessage))
                .andExpect(
                    jsonPath("$.authorization.retrieval_reference_number")
                        .value(response.authorization.retrievalReferenceNumber)
                )
                .andExpect(jsonPath("$.amount.breakdown[0].description").value(response.amount.breakdown[0].description))
                .andExpect(jsonPath("$.amount.breakdown[0].amount").value(response.amount.breakdown[0].amount))
                .andExpect(jsonPath("$.amount.total").value(response.amount.total))
                .andExpect(jsonPath("$.amount.currency").value(response.amount.currency))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.batch").value(response.batch))
                .andExpect(jsonPath("$.host_message").value(response.hostMessage))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.capture.card.holder.name").value(response.capture.card.holder.name))
                .andExpect(jsonPath("$.capture.card.holder.identification.number").value(response.capture.card.holder.identification.number))
                .andExpect(jsonPath("$.capture.card.holder.identification.type").value(response.capture.card.holder.identification.type))
                .andExpect(jsonPath("$.capture.card.masked_pan").value(response.capture.card.maskedPan))
                .andExpect(jsonPath("$.capture.card.icc_data").value(response.capture.card.iccData))
                .andExpect(jsonPath("$.capture.card.card_sequence_number").value(response.capture.card.cardSequenceNumber))
                .andExpect(jsonPath("$.capture.card.bank").value(response.capture.card.bank))
                .andExpect(jsonPath("$.capture.card.type").value(response.capture.card.type))
                .andExpect(jsonPath("$.capture.card.brand").value(response.capture.card.brand))
                .andExpect(jsonPath("$.capture.input_mode").value(response.capture.inputMode))
        }

        scenario("unsuccessful creation") {

            val request = aReimbursementRequest()
            val refund = aRefund()
            val response = aReimbursementResponse()
            val merchantId = ""

            every { createRefundPortIn.execute(request, merchantId) } returns refund
            every { mapper.map(refund) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/refunds"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reverse/refunds"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }

    feature("batch close creation") {

        beforeEach {
            every { httpServletRequest.requestURI } returns "/public/reverse/batch-closes"
        }

        scenario("successful creation") {

            val request = aBatchCloseRequest()
            val batchClose = aBatchClose()
            val response = aBatchCloseResponse()
            val merchantId = "merchant id"

            every { createBatchClosePortIn.execute(request, merchantId) } returns batchClose
            every { mapper.map(batchClose) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/batch-closes"
                )
                    .header("merchantId", merchantId)
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
                .andExpect(jsonPath("$.software_version").value(response.softwareVersion))
                .andExpect(jsonPath("$.total.operation_code").value(response.total.operationCode))
                .andExpect(jsonPath("$.total.currency").value(response.total.currency))
                .andExpect(jsonPath("$.total.amount").value(response.total.amount))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))

        }

        scenario("unsuccessful creation") {

            val request = aBatchCloseRequest()
            val batchClose = aBatchClose()
            val response = aBatchCloseResponse()
            val merchantId = "merchantId"

            every { createBatchClosePortIn.execute(request, merchantId) } returns batchClose
            every { mapper.map(batchClose) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/reverse/batch-closes"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/reverse/batch-closes"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aBatchCloseRequestJson() = """
    {"trace": "123",
     "terminal": {
        "serial_code": "134" 
     },
      "batch": "12434",
      "ticket": "234",
      "total":{ 
         "operation_code": "400",
         "currency":"ARS",
         "amount": "100.00"
        },
      "host_message": "5546546",
      "datetime": "2022-01-19T11:23:23Z",
      "software_version": "v1"
    }
""".trimIndent()

fun aJsonRequest() = """
 {   "payment_id": "1234567890",
     "capture": {
        "card": {
          "bank": "COMAFI",
          "type": "CREDIT",
          "brand": "VISA",
          "card_sequence_number": "20304504",
          "icc_data": "data",
          "holder": {
            "name": "Aixa Halac",
            "identification": {
              "number": "35727828", 
              "type": "DNI"
            }
          },
          "pan": "333344445555",
          "expiration_date": "06/22",
          "cvv": "234"
        },
        "input_mode": "1111"
      },
      "amount": {
        "total": "100.00",
        "currency": "ARS",
        "breakdown": [{
          "description": "descripcion",
          "amount": "1000"
        }]
      },
      "datetime": "2022-01-19T11:23:23Z",
      "trace": "123",
      "ticket": "234",
      "terminal": {
        "serial_code": "134"
      },
      "batch": "12434",
      "installments": "10",
      "host_message": "5546546"
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
