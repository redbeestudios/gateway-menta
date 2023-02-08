package com.menta.apiacquirers.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.apiacquirers.aAcquirerCustomer
import com.menta.apiacquirers.aCustomer
import com.menta.apiacquirers.adapter.`in`.model.OperableResponse
import com.menta.apiacquirers.adapter.`in`.model.mapper.ToOperableResponseMapper
import com.menta.apiacquirers.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.apiacquirers.application.port.`in`.FindCustomerPortIn
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.shared.error.ErrorHandler
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.providers.CurrentResourceProvider
import com.menta.apiacquirers.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

class OperableControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val findCustomerPortIn = mockk<FindCustomerPortIn>()
    val findAcquirerCustomerPortIn = mockk<FindAcquirerCustomerPortIn>()
    val toResponseMapper = mockk<ToOperableResponseMapper>()

    val controller = OperableController(
        findCustomer = findCustomerPortIn,
        findAcquirerCustomerPortIn = findAcquirerCustomerPortIn,
        toOperableResponseMapper = toResponseMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(aControllerAdvice(httpServletRequest))
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("operable controller by customerId") {
        val customerId = customerId
        val country = "ARG"
        val customer = aCustomer()
        val acquirerCustomer = aAcquirerCustomer()

        val response = OperableResponse(
            customerId = customerId,
            acquirers = listOf(
                OperableResponse.Acquirer(
                    acquirerId = "GPS",
                    code = "a code"
                )
            )
        )

        scenario("successful found") {
            every { httpServletRequest.requestURI } returns "/public/acquirers/operable"

            every { findCustomerPortIn.execute(customerId) } returns customer.right()
            every { findAcquirerCustomerPortIn.execute(customerId, country) } returns acquirerCustomer.right()
            every { toResponseMapper.mapFrom(acquirerCustomer) } returns response

            mockMvc
                .perform(
                    get("/public/acquirers/operable?customerId=$customerId")
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.customer_id").value(response.customerId.toString()))
                .andExpect(jsonPath("$.acquirers[0].acquirer_id").value(response.acquirers[0].acquirerId))
                .andExpect(jsonPath("$.acquirers[0].code").value(response.acquirers[0].code))

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
            verify(exactly = 1) { findAcquirerCustomerPortIn.execute(customerId, country) }
            verify(exactly = 1) { toResponseMapper.mapFrom(acquirerCustomer) }
        }
        scenario("unsuccessful by customer NOT FOUND") {
            val error = ApplicationError.customerNotFound(customerId)

            every { httpServletRequest.requestURI } returns "/public/acquirers/operable"
            every { findCustomerPortIn.execute(customerId) } returns error.left()

            mockMvc
                .perform(
                    get("/public/acquirers/operable?customerId=$customerId")
                )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/public/acquirers/operable"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("Customer not found for id $customerId"))

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
            verify(exactly = 0) { findAcquirerCustomerPortIn.execute(customerId, country) }
            verify(exactly = 0) { toResponseMapper.mapFrom(any()) }
        }
    }
})

fun aControllerAdvice(request: HttpServletRequest) = ErrorHandler(
    errorResponseProvider = ErrorResponseProvider(
        currentResourceProvider = CurrentResourceProvider(request),
        metadataProvider = ErrorResponseMetadataProvider(
            currentResourceProvider = CurrentResourceProvider(request)
        )
    )
)
