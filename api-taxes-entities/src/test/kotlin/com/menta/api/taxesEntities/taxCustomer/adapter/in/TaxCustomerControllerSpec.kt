package com.menta.api.taxesEntities.taxCustomer.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.menta.api.taxesEntities.TestConstants.Companion.CUSTOMER_ID
import com.menta.api.taxesEntities.aPreTaxCustomer
import com.menta.api.taxesEntities.aPreTaxCustomerRequest
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxCustomerRequest
import com.menta.api.taxesEntities.aTaxCustomerResponse
import com.menta.api.taxesEntities.adapter.`in`.controller.TaxCustomerController
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreTaxCustomerMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerResponseMapper
import com.menta.api.taxesEntities.anApplicationErrorResponse
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.`in`.FindTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxCustomerPorIn
import com.menta.api.taxesEntities.shared.error.ErrorHandler
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxCustomerDoesNotExists
import com.menta.api.taxesEntities.shared.error.providers.CurrentResourceProvider
import com.menta.api.taxesEntities.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.taxesEntities.shared.error.providers.ErrorResponseProvider
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
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class TaxCustomerControllerSpec : FeatureSpec({
    val findTaxCustomerPortIn = mockk<FindTaxCustomerPortIn>()
    val mapper = mockk<ToTaxCustomerResponseMapper>()
    val errorProvider = mockk<ErrorResponseProvider>()
    val createTaxCustomerPortIn = mockk<CreateTaxCustomerPortIn>()
    val toTaxCustomerMapper = mockk<ToTaxCustomerMapper>()
    val toPreTaxCustomerMapper = mockk<ToPreTaxCustomerMapper>()
    val updateTaxCustomerPorIn = mockk<UpdateTaxCustomerPorIn>()

    val controller = TaxCustomerController(
        findTaxCustomerPortIn = findTaxCustomerPortIn,
        createTaxCustomerPortIn = createTaxCustomerPortIn,
        toResponseMapper = mapper,
        toTaxCustomerMapper = toTaxCustomerMapper,
        errorResponseProvider = errorProvider,
        toPreTaxCustomerMapper = toPreTaxCustomerMapper,
        updateTaxCustomerPorIn = updateTaxCustomerPorIn
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
            .setControllerAdvice(
                com.menta.api.taxesEntities.taxMerchant.adapter.`in`.aControllerAdvice(
                    httpServletRequest
                )
            )
            .setMessageConverters(messageConverter)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get tax customer by customerId") {

        scenario("tax merchant found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val taxCustomer = aTaxCustomer()
            val response = aTaxCustomerResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer/$customerId"
            every { findTaxCustomerPortIn.execute(customerId) } returns taxCustomer.right()
            every { mapper.mapFrom(taxCustomer) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/taxes-entities/customer/$customerId"
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(response.id.toString()))

            verify(exactly = 1) { findTaxCustomerPortIn.execute(customerId) }
            verify(exactly = 1) { mapper.mapFrom(taxCustomer) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }

    feature("create new tax customer") {

        scenario("with customer valid") {
            val taxCustomerRequest = aTaxCustomerRequest()
            val taxCustomer = aTaxCustomer()
            val taxCustomerSaved = aTaxCustomer().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))
            val taxCustomerResponse = aTaxCustomerResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer"
            every { toTaxCustomerMapper.mapFrom(taxCustomerRequest) } returns taxCustomer
            every { createTaxCustomerPortIn.execute(taxCustomer) } returns taxCustomerSaved.right()
            every { mapper.mapFrom(taxCustomerSaved) } returns taxCustomerResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(taxCustomerRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(taxCustomerResponse.id.toString()))
                .andExpect(jsonPath("$.customer_id").value(taxCustomerResponse.customerId.toString()))
                .andExpect(jsonPath("$.country").value(taxCustomerResponse.country.name))
                .andExpect(jsonPath("$.tax_condition").value(taxCustomerResponse.taxCondition.name))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(taxCustomerResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.fee_rules[0].payment_method").value(taxCustomerResponse.feeRules?.get(0)?.paymentMethod?.name))
                .andExpect(jsonPath("$.fee_rules[0].term").value(taxCustomerResponse.feeRules?.get(0)?.term))
                .andExpect(jsonPath("$.fee_rules[0].installments").value(taxCustomerResponse.feeRules?.get(0)?.installments))
                .andExpect(jsonPath("$.fee_rules[0].commission").value(taxCustomerResponse.feeRules!![0].commission))

            verify(exactly = 1) { toTaxCustomerMapper.mapFrom(taxCustomerRequest) }
            verify(exactly = 1) { createTaxCustomerPortIn.execute(taxCustomer) }
            verify(exactly = 1) { mapper.mapFrom(taxCustomerSaved) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("with invalid country return BAD REQUEST") {
            val customerRequest = aTaxCustomerRequest()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule())
                            .setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(customerRequest)
                            .replace("customer_id", "INVALID_MERCHANT_ID")
                    )
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("012"))
                .andExpect(jsonPath("errors[0].resource").value("/private/taxes-entities/customer"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").isNotEmpty)
        }
    }

    feature("update tax customer") {

        scenario("Ok") {
            val customerId = UUID.fromString(CUSTOMER_ID)
            val preTaxCustomerRequest = aPreTaxCustomerRequest()
            val preTaxCustomer = aPreTaxCustomer()
            val taxCustomerUpdated = aTaxCustomer()
            val taxCustomerResponse = aTaxCustomerResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer/$CUSTOMER_ID"
            every { toPreTaxCustomerMapper.map(preTaxCustomerRequest) } returns preTaxCustomer
            every { updateTaxCustomerPorIn.execute(preTaxCustomer, customerId) } returns taxCustomerUpdated.right()
            every { mapper.mapFrom(taxCustomerUpdated) } returns taxCustomerResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/private/taxes-entities/customer/$CUSTOMER_ID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(preTaxCustomerRequest)
                    )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(taxCustomerResponse.id.toString()))
                .andExpect(jsonPath("$.customer_id").value(taxCustomerResponse.customerId.toString()))
                .andExpect(jsonPath("$.country").value(taxCustomerResponse.country.name))
                .andExpect(jsonPath("$.tax_condition").value(taxCustomerResponse.taxCondition.name))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(taxCustomerResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.fee_rules[0].payment_method").value(taxCustomerResponse.feeRules?.get(0)?.paymentMethod?.name))
                .andExpect(jsonPath("$.fee_rules[0].term").value(taxCustomerResponse.feeRules?.get(0)?.term))
                .andExpect(jsonPath("$.fee_rules[0].installments").value(taxCustomerResponse.feeRules?.get(0)?.installments))
                .andExpect(jsonPath("$.fee_rules[0].commission").value(taxCustomerResponse.feeRules?.get(0)?.commission))

            verify(exactly = 1) { toPreTaxCustomerMapper.map(preTaxCustomerRequest) }
            verify(exactly = 1) { updateTaxCustomerPorIn.execute(preTaxCustomer, customerId) }
            verify(exactly = 1) { mapper.mapFrom(taxCustomerUpdated) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("Error when Tax Customer not found") {
            val customerId = UUID.fromString(CUSTOMER_ID)
            val preTaxCustomerRequest = aPreTaxCustomerRequest()
            val preTaxCustomer = aPreTaxCustomer()
            val error = taxCustomerDoesNotExists()
            val errorResponse = anApplicationErrorResponse(error)

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer/$CUSTOMER_ID"
            every { toPreTaxCustomerMapper.map(preTaxCustomerRequest) } returns preTaxCustomer
            every { updateTaxCustomerPorIn.execute(preTaxCustomer, customerId) } returns error.left()
            every { errorProvider.provideFor(error) } returns errorResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/private/taxes-entities/customer/$CUSTOMER_ID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(preTaxCustomerRequest)

                    )
            ).andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("301"))
                .andExpect(jsonPath("errors[0].message").value("Tax customer does not exists"))
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
