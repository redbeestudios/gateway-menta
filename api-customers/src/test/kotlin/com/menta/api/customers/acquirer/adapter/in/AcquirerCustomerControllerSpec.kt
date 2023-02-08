package com.menta.api.customers.acquirer.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.menta.api.customers.aCustomerId
import com.menta.api.customers.aPreAcquirerCustomer
import com.menta.api.customers.acquirer.adapter.`in`.controller.AcquirerCustomerController
import com.menta.api.customers.acquirer.adapter.`in`.model.mapper.ToAcquirerCustomerResponseMapper
import com.menta.api.customers.acquirer.adapter.`in`.model.mapper.ToPreAcquirerCustomerMapper
import com.menta.api.customers.acquirer.application.port.`in`.CreateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.`in`.UpdateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.domain.provider.AcquirerProvider
import com.menta.api.customers.anAcquirer
import com.menta.api.customers.anAcquirerCustomer
import com.menta.api.customers.anAcquirerCustomerRequest
import com.menta.api.customers.anAcquirerCustomerResponse
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import com.menta.api.customers.shared.error.providers.CurrentResourceProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.util.Optional
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class AcquirerCustomerControllerSpec : FeatureSpec({
    val findAcquirerCustomerPortIn = mockk<FindAcquirerCustomerPortIn>()
    val createAcquirerCustomerPortIn = mockk<CreateAcquirerCustomerPortIn>()
    val updateAcquirerCustomerPortIn = mockk<UpdateAcquirerCustomerPortIn>()
    val toPreAcquirerMapper = mockk<ToPreAcquirerCustomerMapper>()
    val errorProvider = mockk<ErrorResponseProvider>()
    val acquirerProvider = mockk<AcquirerProvider>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()

    val controller = AcquirerCustomerController(
        findAcquirerCustomer = findAcquirerCustomerPortIn,
        toResponseMapper = ToAcquirerCustomerResponseMapper(),
        acquirerProvider = acquirerProvider,
        createAcquirerCustomer = createAcquirerCustomerPortIn,
        updateAcquirerCustomer = updateAcquirerCustomerPortIn,
        toPreAcquirerCustomerMapper = toPreAcquirerMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(SNAKE_CASE)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)

    val httpServletRequest = mockk<HttpServletRequest>()
    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(com.menta.api.customers.customer.adapter.`in`.aControllerAdvice(httpServletRequest))
            .setMessageConverters(messageConverter)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get acquirer customer by customerId and acquirerId") {

        scenario("acquirer customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "GPS"
            val acquirerCustomer = anAcquirerCustomer()
            val acquirer = anAcquirer()
            val response = anAcquirerCustomerResponse()

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId/acquirers/$acquirerId"
            every { findAcquirerCustomerPortIn.execute(customerId, acquirerId) } returns acquirerCustomer.right()
            every { acquirerProvider.provideFor(acquirerId) } returns acquirer.right()

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/customers/$customerId/acquirers/$acquirerId"
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.customer_id").value(response.customerId))
                .andExpect(jsonPath("$.acquirer_id").value(response.acquirerId))
                .andExpect(jsonPath("$.code").value(response.code))

            verify(exactly = 1) { findAcquirerCustomerPortIn.execute(customerId, acquirerId) }
            verify(exactly = 1) { acquirerProvider.provideFor(acquirerId) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("acquirer customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "GPS"
            val acquirer = anAcquirer()
            val error = acquirerCustomerNotFound(customerId, acquirerId)

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId/acquirers/$acquirerId"
            every { findAcquirerCustomerPortIn.execute(customerId, acquirerId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/customers/$customerId/acquirers/$acquirerId"
            every { acquirerProvider.provideFor(acquirerId) } returns acquirer.right()
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/customers/$customerId/acquirers/$acquirerId"
                )
            ).andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/$customerId/acquirers/$acquirerId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findAcquirerCustomerPortIn.execute(customerId, acquirerId) }
            verify(exactly = 1) { acquirerProvider.provideFor(acquirerId) }
        }
    }

    feature("create new acquirer customer") {

        scenario("with acquirer customer") {
            val acquirerCustomerRequest = anAcquirerCustomerRequest()
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomerSaved = anAcquirerCustomer()
            val acquirerCustomerResponse = anAcquirerCustomerResponse()
            val acquirer = anAcquirer()

            every { httpServletRequest.requestURI } returns "/private/customers/$aCustomerId/acquirers/"
            every { findAcquirerCustomerPortIn.find(aCustomerId, "GPS") } returns Optional.empty()
            every { acquirerProvider.provideFor("GPS") } returns acquirer.right()
            every { toPreAcquirerMapper.map(acquirerCustomerRequest, aCustomerId) } returns aPreAcquirerCustomer()
            every {
                createAcquirerCustomerPortIn.execute(
                    preAcquirerCustomer,
                    Optional.empty()
                )
            } returns acquirerCustomerSaved.right()

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/customers/$aCustomerId/acquirers/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(acquirerCustomerRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.customer_id").value(acquirerCustomerResponse.customerId))
                .andExpect(jsonPath("$.acquirer_id").value(acquirerCustomerResponse.acquirerId))
                .andExpect(jsonPath("$.code").value(acquirerCustomerResponse.code))

            verify(exactly = 1) { createAcquirerCustomerPortIn.execute(preAcquirerCustomer, Optional.empty()) }
            verify(exactly = 1) { findAcquirerCustomerPortIn.find(aCustomerId, "GPS") }
            verify(exactly = 1) { acquirerProvider.provideFor("GPS") }
            verify(exactly = 1) { toPreAcquirerMapper.map(acquirerCustomerRequest, aCustomerId) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }
})
