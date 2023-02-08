package com.kiwi.api.batchcloses.hexagonal.adapter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.kiwi.api.batchcloses.hexagonal.adapter.controller.mapper.ToBatchCloseResponseMapper
import com.kiwi.api.batchcloses.hexagonal.application.*
import com.kiwi.api.batchcloses.hexagonal.application.usecase.crud.CreateBatchCloseUseCase
import com.kiwi.api.batchcloses.shared.error.ErrorHandler
import com.kiwi.api.batchcloses.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.batchcloses.shared.error.providers.CurrentResourceProvider
import com.kiwi.api.batchcloses.shared.error.providers.ErrorResponseMetadataProvider
import com.kiwi.api.batchcloses.shared.error.providers.ErrorResponseProvider
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

class BatchCloseControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val mapper: ToBatchCloseResponseMapper = mockk()
    val useCase: CreateBatchCloseUseCase = mockk()

    val controller = BatchCloseController(useCase, mapper)

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/public/batch-closes"
        every { httpServletRequest.queryString } returns null
    }

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    feature("batch close creation") {

        scenario("successful creation") {

            val request = aBatchCloseRequest()
            val batchClose = aBatchClose()
            val response = aBatchCloseResponse()
            val json = aJsonRequest()

            every { useCase.execute(request, merchantId) } returns batchClose
            every { mapper.map(batchClose) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/batch-closes"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(response.id))
                .andExpect(jsonPath("$.status.code").value(response.status.code))
                .andExpect(jsonPath("$.status.situation").doesNotExist())
                .andExpect(jsonPath("$.authorization.code").value(response.authorization.code))
                .andExpect(jsonPath("$.authorization.retrieval_reference_number").value(response.authorization.retrievalReferenceNumber))
                .andExpect(jsonPath("$.datetime").value(response.datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.trace").value(response.trace))
                .andExpect(jsonPath("$.ticket").value(response.ticket))
                .andExpect(jsonPath("$.batch").value(response.batch))
                .andExpect(jsonPath("$.merchant.id").value(response.merchant.id))
                .andExpect(jsonPath("$.terminal.id").value(response.terminal.id))
                .andExpect(jsonPath("$.terminal.serial_code").value(response.terminal.serialCode))
                .andExpect(jsonPath("$.terminal.software_version").value(response.terminal.softwareVersion))
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
        }

        scenario("unsuccessful creation") {

            val request = aBatchCloseRequest()
            val payment = aBatchClose()
            val response = aBatchCloseResponse()

            every { useCase.execute(request, merchantId) } returns payment
            every { mapper.map(payment) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/public/batch-closes"
                )
                    .header("merchantId", merchantId)
                    .contentType(APPLICATION_JSON)
                    .content(anInvalidRequest())
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(MESSAGE_NOT_READABLE.value))
                .andExpect(jsonPath("errors[0].resource").value("/public/batch-closes"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }
    }
})

fun aJsonRequest() = """
    {
    	"datetime": "$datetime",
        "trace": "123",
        "ticket": "234",
        "batch": "234",
        "terminal": {
            "serial_code": "134",
            "software_version": "1.0.456"
        },
        "host_message": "5566447788",
        "totals": [
            {
                "operation_code": "PAYMENT",
                "amount": 123456700,
                "currency": "ARS"
            },
            {
                "operation_code": "ANNULMENT",
                "amount": 234500,
                "currency": "ARS"
            },
            {
                "operation_code": "REFUND",
                "amount": 78900,
                "currency": "ARS"
            }
        ]
    }
""".trimIndent()

fun anInvalidRequest() = aJsonRequest().replace("\"currency\": \"ARS\"", "")

fun aControllerAdvice(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )
