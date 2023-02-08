package com.menta.api.taxesEntities.taxMerchant.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.menta.api.taxesEntities.TestConstants.Companion.MERCHANT_ID
import com.menta.api.taxesEntities.aPreTaxMerchant
import com.menta.api.taxesEntities.aPreTaxMerchantRequest
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.aTaxMerchantRequest
import com.menta.api.taxesEntities.aTaxMerchantResponse
import com.menta.api.taxesEntities.adapter.`in`.controller.TaxMerchantController
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreMerchantMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantResponseMapper
import com.menta.api.taxesEntities.anApplicationErrorResponse
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.`in`.FindTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxMerchantPortIn
import com.menta.api.taxesEntities.shared.error.ErrorHandler
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxMerchantDoesNotExists
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

class TaxMerchantControllerSpec : FeatureSpec({
    val findTaxMerchantPortIn = mockk<FindTaxMerchantPortIn>()
    val mapper = mockk<ToTaxMerchantResponseMapper>()
    val errorProvider = mockk<ErrorResponseProvider>()
    val createTaxMerchantPortIn = mockk<CreateTaxMerchantPortIn>()
    val toTaxMerchantMapper = mockk<ToTaxMerchantMapper>()
    val updateTaxMerchantPortIn = mockk<UpdateTaxMerchantPortIn>()
    val toPreMerchantMapper = mockk<ToPreMerchantMapper>()

    val controller = TaxMerchantController(
        findTaxMerchantPortIn = findTaxMerchantPortIn,
        createTaxMerchantPortIn = createTaxMerchantPortIn,
        toResponseMapper = mapper,
        toTaxMerchantMapper = toTaxMerchantMapper,
        errorResponseProvider = errorProvider,
        updateTaxMerchantPortIn = updateTaxMerchantPortIn,
        toPreMerchantMapper = toPreMerchantMapper
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
            .setControllerAdvice(aControllerAdvice(httpServletRequest))
            .setMessageConverters(messageConverter)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get tax merchant by merchantId") {

        scenario("tax merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchant = aTaxMerchant()
            val response = aTaxMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant/$merchantId"
            every { findTaxMerchantPortIn.execute(merchantId) } returns merchant.right()
            every { mapper.mapFrom(merchant) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/taxes-entities/merchant/$merchantId"
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(response.id.toString()))

            verify(exactly = 1) { findTaxMerchantPortIn.execute(merchantId) }
            verify(exactly = 1) { mapper.mapFrom(merchant) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }

    feature("create new tax merchant") {

        scenario("with merchant valid") {
            val taxMerchantRequest = aTaxMerchantRequest()
            val taxMerchant = aTaxMerchant()
            val taxMerchantSaved = aTaxMerchant().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))
            val taxMerchantResponse = aTaxMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant"
            every { toTaxMerchantMapper.mapFrom(taxMerchantRequest) } returns taxMerchant
            every { createTaxMerchantPortIn.execute(taxMerchant) } returns taxMerchantSaved.right()
            every { mapper.mapFrom(taxMerchantSaved) } returns taxMerchantResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/merchant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(taxMerchantRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(taxMerchantResponse.id.toString()))
                .andExpect(jsonPath("$.merchant_id").value(taxMerchantResponse.merchantId.toString()))
                .andExpect(jsonPath("$.country").value(taxMerchantResponse.country.name))
                .andExpect(jsonPath("$.tax_condition").value(taxMerchantResponse.taxCondition.name))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(taxMerchantResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.fee_rules[0].payment_method").value(taxMerchantResponse.feeRules?.get(0)?.paymentMethod?.name))
                .andExpect(jsonPath("$.fee_rules[0].term").value(taxMerchantResponse.feeRules!![0].term))
                .andExpect(jsonPath("$.fee_rules[0].installments").value(taxMerchantResponse.feeRules!![0].installments))
                .andExpect(jsonPath("$.fee_rules[0].commission").value(taxMerchantResponse.feeRules!![0].commission))

            verify(exactly = 1) { toTaxMerchantMapper.mapFrom(taxMerchantRequest) }
            verify(exactly = 1) { createTaxMerchantPortIn.execute(taxMerchant) }
            verify(exactly = 1) { mapper.mapFrom(taxMerchantSaved) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("with invalid country return BAD REQUEST") {
            val merchantRequest = aTaxMerchantRequest()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/merchant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule())
                            .setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(merchantRequest)
                            .replace("merchant_id", "INVALID_MERCHANT")
                    )
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("012"))
                .andExpect(jsonPath("errors[0].resource").value("/private/taxes-entities/merchant"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").isNotEmpty)
        }
    }

    feature("update tax merchant") {

        scenario("Ok") {
            val merchantId = UUID.fromString(MERCHANT_ID)
            val preTaxMerchantRequest = aPreTaxMerchantRequest()
            val preTaxMerchant = aPreTaxMerchant()
            val taxMerchantUpdated = aTaxMerchant()
            val taxMerchantResponse = aTaxMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant/$MERCHANT_ID"
            every { toPreMerchantMapper.map(preTaxMerchantRequest) } returns preTaxMerchant
            every { updateTaxMerchantPortIn.execute(preTaxMerchant, merchantId) } returns taxMerchantUpdated.right()
            every { mapper.mapFrom(taxMerchantUpdated) } returns taxMerchantResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/private/taxes-entities/merchant/$MERCHANT_ID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(preTaxMerchantRequest)
                    )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(taxMerchantResponse.id.toString()))
                .andExpect(jsonPath("$.merchant_id").value(taxMerchantResponse.merchantId.toString()))
                .andExpect(jsonPath("$.customer_id").value(taxMerchantResponse.customerId.toString()))
                .andExpect(jsonPath("$.country").value(taxMerchantResponse.country.name))
                .andExpect(jsonPath("$.tax_condition").value(taxMerchantResponse.taxCondition.name))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(taxMerchantResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.fee_rules[0].payment_method").value(taxMerchantResponse.feeRules?.get(0)?.paymentMethod?.name))
                .andExpect(jsonPath("$.fee_rules[0].term").value(taxMerchantResponse.feeRules?.get(0)?.term))
                .andExpect(jsonPath("$.fee_rules[0].installments").value(taxMerchantResponse.feeRules?.get(0)?.installments))
                .andExpect(jsonPath("$.fee_rules[0].commission").value(taxMerchantResponse.feeRules?.get(0)?.commission))

            verify(exactly = 1) { toPreMerchantMapper.map(preTaxMerchantRequest) }
            verify(exactly = 1) { updateTaxMerchantPortIn.execute(preTaxMerchant, merchantId) }
            verify(exactly = 1) { mapper.mapFrom(taxMerchantUpdated) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("Error when Tax Customer not found") {
            val merchantId = UUID.fromString(MERCHANT_ID)
            val preTaxMerchantRequest = aPreTaxMerchantRequest()
            val preTaxMerchant = aPreTaxMerchant()
            val error = taxMerchantDoesNotExists()
            val errorResponse = anApplicationErrorResponse(error)

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant/$MERCHANT_ID"
            every { toPreMerchantMapper.map(preTaxMerchantRequest) } returns preTaxMerchant
            every { updateTaxMerchantPortIn.execute(preTaxMerchant, merchantId) } returns error.left()
            every { errorProvider.provideFor(error) } returns errorResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/private/taxes-entities/merchant/$MERCHANT_ID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(preTaxMerchantRequest)

                    )
            ).andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("301"))
                .andExpect(jsonPath("errors[0].message").value("Tax merchant does not exists"))
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
