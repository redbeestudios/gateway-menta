package com.menta.api.transactions.adapter.`in`.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.transactions.TestConstants.Companion.TERMINAL_ID
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.`in`.model.mapper.ToTransactionResponseMapper
import com.menta.api.transactions.anApiErrorResponse
import com.menta.api.transactions.application.port.`in`.FindTransactionByFilterInPort
import com.menta.api.transactions.shared.error.ErrorHandler
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import com.menta.api.transactions.shared.error.providers.CurrentResourceProvider
import com.menta.api.transactions.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.transactions.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class TransactionControllerSpec : FeatureSpec({
    lateinit var findTransactionByFilter: FindTransactionByFilterInPort
    lateinit var errorResponseProvider: ErrorResponseProvider
    lateinit var toTransactionResponseMapper: ToTransactionResponseMapper
    lateinit var controller: TransactionController
    lateinit var mockMvc: MockMvc
    lateinit var objectMapper: MappingJackson2HttpMessageConverter
    val httpServletRequest: HttpServletRequest = mockk()
    val page = 0
    val size = 10

    beforeEach {
        findTransactionByFilter = mockk()
        errorResponseProvider = mockk()
        toTransactionResponseMapper = ToTransactionResponseMapper()
        objectMapper = Jackson2ObjectMapperBuilder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
            .build<ObjectMapper>()
            .let { MappingJackson2HttpMessageConverter(it) }
        controller = TransactionController(
            findTransactionByFilter,
            errorResponseProvider,
            toTransactionResponseMapper
        )
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(objectMapper)
            .build()

    }

    feature("get transaction by terminal id") {

        val terminalId = UUID.fromString(TERMINAL_ID)
        val transactionId = UUID.fromString(TRANSACTION_ID)
        var transaction = aTransaction(transactionId)
        val result = PageImpl(listOf(transaction))

        val uri = "/public/transactions?terminalId=$TERMINAL_ID&page=$page&size=$size"
        every { httpServletRequest.requestURI } returns uri

        scenario("terminal found") {

            every {
                findTransactionByFilter.execute(
                    null,
                    null,
                    null,
                    null,
                    null,
                    terminalId,
                    null,
                    null,
                    page,
                    size,
                    null
                )
            } returns result.right()

            mockMvc.perform(get(uri))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].id").value(transaction.id.toString()))
                .andExpect(
                    jsonPath("$.content[0].merchant_id").value(transaction.merchantId.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].customer_id").value(transaction.customerId.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].terminal.id").value(transaction.terminal.id.toString())
                )
                .andExpect(jsonPath("$.content[0].currency").value(transaction.currency))
                .andExpect(
                    jsonPath("$.content[0].operation.amount").value(transaction.operation.amount)
                )
                .andExpect(
                    jsonPath("$.content[0].operation.id")
                        .value(transaction.operation.id.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].operation.type")
                        .value(transaction.operation.type.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].operation.acquirer_id")
                        .value(transaction.operation.acquirerId)
                )
                .andExpect(
                    jsonPath("$.content[0].operation.status")
                        .value(transaction.operation.status.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].operation.datetime")
                        .value(transaction.operation.datetime.toString())
                )
                .andExpect(
                    jsonPath("$.content[0].installment.number")
                        .value(transaction.installment.number)
                )
                .andExpect(
                    jsonPath("$.content[0].installment.plan").value(transaction.installment.plan)
                )
                .andExpect(jsonPath("$.content[0].amount").value(transaction.amount))
                .andExpect(jsonPath("$.content[0].refunded_amount").doesNotExist())
                .andExpect(
                    jsonPath("$.content[0].card.type").value(transaction.card?.type.toString())
                )
                .andExpect(jsonPath("$.content[0].card.masked_pan").value(transaction.card?.mask))
                .andExpect(jsonPath("$.content[0].card.brand").value(transaction.card?.brand))
                .andExpect(jsonPath("$.content[0].card.bank").value(transaction.card?.bank))
                .andExpect(
                    jsonPath("$.content[0].card.holder.name")
                        .value(transaction.card?.holder?.name)
                )
                .andExpect(
                    jsonPath("$.content[0].card.holder.document")
                        .value(transaction.card?.holder?.document)
                )


            verify(exactly = 1) {
                findTransactionByFilter.execute(
                    null,
                    null,
                    null,
                    null,
                    null,
                    terminalId,
                    null,
                    null,
                    page,
                    size,
                    null
                )
            }
        }
    }
    feature("Not Found Transaction") {

        val start = OffsetDateTime.of(2022, 7, 1, 23, 23, 23, 0, ZoneOffset.UTC)
        val end = OffsetDateTime.of(2022, 7, 19, 23, 23, 23, 0, ZoneOffset.UTC)
        val transactionId = UUID.fromString(TRANSACTION_ID)
        val anApiErrorResponse = anApiErrorResponse()
        val error = operationNotFound(
            null,
            null,
            transactionId,
            null,
            null,
            null,
            null,
            start,
            end
        )

        val uri = "/public/transactions?transactionId=$TRANSACTION_ID&start=$start&page=$page&size=$size&end=${end}"
        every { httpServletRequest.requestURI } returns uri

        scenario("terminal found") {

            every {
                findTransactionByFilter.execute(
                    null,
                    null,
                    transactionId,
                    null,
                    null,
                    null,
                    any(),
                    any(),
                    page,
                    size,
                    null
                )
            } returns error.left()
            every { errorResponseProvider.provideFor(error) } returns anApiErrorResponse

            mockMvc.perform(get(uri))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value(anApiErrorResponse.errors[0].code))
                .andExpect(jsonPath("errors[0].resource").value(anApiErrorResponse.errors[0].resource))
                .andExpect(jsonPath("errors[0].metadata.a_key").value(anApiErrorResponse.errors[0].metadata["a_key"]))
                .andExpect(jsonPath("errors[0].message").value(anApiErrorResponse.errors[0].message))

            verify(exactly = 1) { errorResponseProvider.provideFor(error) }
            verify(exactly = 1) {
                findTransactionByFilter.execute(
                    null,
                    null,
                    transactionId,
                    null,
                    null,
                    null,
                    any(),
                    any(),
                    page,
                    size,
                    null
                )
            }
        }
    }
})

fun aControllerAdvice(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )