package com.menta.api.taxesEntities.feeRule.adapter.`in`

import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.menta.api.taxesEntities.TestConstants
import com.menta.api.taxesEntities.aFeeRuleRequest
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.adapter.`in`.controller.FeeRuleController
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToFeeRuleMapper
import com.menta.api.taxesEntities.application.port.`in`.AddFeeRulePortIn
import com.menta.api.taxesEntities.shared.error.providers.ErrorResponseProvider
import com.menta.api.taxesEntities.taxCustomer.adapter.`in`.aControllerAdvice
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class FeeRuleControllerSpec : FeatureSpec({
    val errorProvider = mockk<ErrorResponseProvider>()
    val addFeeRulePortIn: AddFeeRulePortIn = mockk()
    val toFeeRuleMapper: ToFeeRuleMapper = mockk()

    val controller = FeeRuleController(
        addFeeRulePortIn = addFeeRulePortIn,
        toFeeRuleMapper = toFeeRuleMapper,
        errorResponseProvider = errorProvider
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
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
                aControllerAdvice(
                    httpServletRequest
                )
            )
            .setMessageConverters(messageConverter)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("add merchant fee rule option to a customer") {

        scenario("add merchant fee rule option to a customer customer valid") {
            val aFeeRuleRequest = aFeeRuleRequest()
            val taxCustomer = aTaxCustomer()
            val merchantFeeRulesOptions = aTaxMerchant().feeRules?.get(0)

            every { toFeeRuleMapper.mapFrom(aFeeRuleRequest) } returns merchantFeeRulesOptions!!
            every { addFeeRulePortIn.add(UUID.fromString(TestConstants.CUSTOMER_ID), merchantFeeRulesOptions) } returns merchantFeeRulesOptions.right()
            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer/${TestConstants.CUSTOMER_ID}/feeRule"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/customer/${TestConstants.CUSTOMER_ID}/feeRule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(aFeeRuleRequest)
                    )
            ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(merchantFeeRulesOptions.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.payment_method").value(merchantFeeRulesOptions.paymentMethod.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.term").value(merchantFeeRulesOptions.term))
                .andExpect(MockMvcResultMatchers.jsonPath("$.installments").value(merchantFeeRulesOptions.installments))
                .andExpect(MockMvcResultMatchers.jsonPath("$.commission").value(merchantFeeRulesOptions.commission))
                .andExpect(MockMvcResultMatchers.jsonPath("$.menta_commission").value(merchantFeeRulesOptions.mentaCommission))

            verify { toFeeRuleMapper.mapFrom(aFeeRuleRequest) }
            verify { addFeeRulePortIn.add(taxCustomer.customerId, taxCustomer.merchantFeeRulesOptions?.get(0)!!) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("with invalid payment_method return BAD REQUEST") {
            val feeRuleRequest = aFeeRuleRequest()

            every { httpServletRequest.requestURI } returns "/private/taxes-entities/customer/${TestConstants.CUSTOMER_ID}/feeRule"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/customer/${TestConstants.CUSTOMER_ID}/feeRule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule())
                            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                            .writeValueAsString(feeRuleRequest)
                            .replace("payment_method", "INVALID_PAYMENT_METHOD")
                    )
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("datetime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].code").value("012"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].resource").value("/private/taxes-entities/customer/${TestConstants.CUSTOMER_ID}/feeRule"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").isNotEmpty)
        }
    }

    feature("select active merchant fee rule") {

        scenario("select merchant fee rule to a merchant valid") {
            val merchantFeeRulesOptions = aTaxMerchant().feeRules?.get(0)
            val ids = listOf(merchantFeeRulesOptions!!.id)
            every { addFeeRulePortIn.select(UUID.fromString(TestConstants.MERCHANT_ID), ids) } returns listOf(merchantFeeRulesOptions).right()
            every { httpServletRequest.requestURI } returns "/private/taxes-entities/merchant/${TestConstants.MERCHANT_ID}/select"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/taxes-entities/merchant/${TestConstants.MERCHANT_ID}/select")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(ids)
                    )
            ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(merchantFeeRulesOptions.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].payment_method").value(merchantFeeRulesOptions.paymentMethod.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].term").value(merchantFeeRulesOptions.term))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].installments").value(merchantFeeRulesOptions.installments))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].commission").value(merchantFeeRulesOptions.commission))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].menta_commission").value(merchantFeeRulesOptions.mentaCommission))

            verify { addFeeRulePortIn.select(UUID.fromString(TestConstants.MERCHANT_ID), ids) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
    }
})
