package com.menta.api.merchants.acquirer.adapter.`in`.controller

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.menta.api.merchants.aPreAcquirerMerchant
import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.acquirer.adapter.`in`.model.mapper.ToAcquirerMerchantResponseMapper
import com.menta.api.merchants.acquirer.adapter.`in`.model.mapper.ToPreAcquirerMerchantMapper
import com.menta.api.merchants.acquirer.anAcquirer
import com.menta.api.merchants.acquirer.anAcquirerId
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.anAcquirerMerchantResponse
import com.menta.api.merchants.acquirer.application.port.`in`.CreateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.`in`.FindAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.`in`.UpdateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.domain.provider.AcquirerProvider
import com.menta.api.merchants.anAcquirerMerchantRequest
import com.menta.api.merchants.shared.error.ErrorHandler
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantNotFound
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidAcquirer
import com.menta.api.merchants.shared.error.providers.CurrentResourceProvider
import com.menta.api.merchants.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.merchants.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.util.Optional
import javax.servlet.http.HttpServletRequest

class AcquirerMerchantControllerSpec : FeatureSpec({
    val findAcquirerMerchantPortIn = mockk<FindAcquirerMerchantPortIn>()
    val createAcquirerMerchantPortIn = mockk<CreateAcquirerMerchantPortIn>()
    val updateAcquirerMerchantPortIn = mockk<UpdateAcquirerMerchantPortIn>()
    val mapper = mockk<ToAcquirerMerchantResponseMapper>()
    val toPreAcquirerMapper = mockk<ToPreAcquirerMerchantMapper>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()
    val acquirerProvider = mockk<AcquirerProvider>()

    val controller = AcquirerMerchantController(
        findAcquirerMerchant = findAcquirerMerchantPortIn,
        toResponseMapper = mapper,
        acquirerProvider = acquirerProvider,
        createAcquirerMerchant = createAcquirerMerchantPortIn,
        updateAcquirerMerchant = updateAcquirerMerchantPortIn,
        toPreAcquirerMerchantMapper = toPreAcquirerMapper
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

    feature("get merchant by acquirer and merchant id ") {

        val uri = "/private/merchants/$aMerchantId/acquirers/$anAcquirerId"
        every { httpServletRequest.requestURI } returns uri

        scenario("merchant found") {

            every { acquirerProvider.provideFor(anAcquirerId) } returns anAcquirer.right()
            every { findAcquirerMerchantPortIn.execute(anAcquirerId, aMerchantId) } returns anAcquirerMerchant.right()
            every { mapper.mapFrom(anAcquirerMerchant) } returns anAcquirerMerchantResponse

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.merchant_id").value(anAcquirerMerchantResponse.merchantId.toString()))
                .andExpect(jsonPath("$.acquirer").value(anAcquirerMerchantResponse.acquirer))
                .andExpect(jsonPath("$.code").value(anAcquirerMerchantResponse.code))

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 1) { findAcquirerMerchantPortIn.execute(anAcquirerId, aMerchantId) }
            verify(exactly = 1) { mapper.mapFrom(anAcquirerMerchant) }
        }

        scenario("invalid acquirer") {

            val error = invalidAcquirer(anAcquirerId)

            every { httpServletRequest.requestURI } returns uri
            every { acquirerProvider.provideFor(anAcquirerId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(uri))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 0) { findAcquirerMerchantPortIn.execute(anAcquirerId, aMerchantId) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }

        scenario("merchant NOT found") {

            val error = acquirerMerchantNotFound(anAcquirerId, aMerchantId)

            every { httpServletRequest.requestURI } returns uri
            every { acquirerProvider.provideFor(anAcquirerId) } returns anAcquirer.right()
            every { findAcquirerMerchantPortIn.execute(anAcquirerId, aMerchantId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                get(uri)
            )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(uri))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { acquirerProvider.provideFor(anAcquirerId) }
            verify(exactly = 1) { findAcquirerMerchantPortIn.execute(anAcquirerId, aMerchantId) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
    }

    feature("create new acquirer merchant") {

        scenario("with acquirer merchant") {
            val acquirerMerchantRequest = anAcquirerMerchantRequest()
            val preAcquirerMerchant = aPreAcquirerMerchant()
            val acquirerMerchantSaved = anAcquirerMerchant
            val acquirerMerchantResponse = anAcquirerMerchantResponse
            val acquirer = anAcquirer

            every { httpServletRequest.requestURI } returns "/private/merchants/$aMerchantId/acquirers/"
            every { findAcquirerMerchantPortIn.find(aMerchantId, "GPS") } returns Optional.empty()
            every { acquirerProvider.provideFor("GPS") } returns acquirer.right()
            every { toPreAcquirerMapper.map(acquirerMerchantRequest, aMerchantId) } returns preAcquirerMerchant
            every { mapper.mapFrom(acquirerMerchantSaved) } returns acquirerMerchantResponse
            every {
                createAcquirerMerchantPortIn.execute(
                    preAcquirerMerchant,
                    Optional.empty()
                )
            } returns acquirerMerchantSaved.right()

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/merchants/$aMerchantId/acquirers/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(acquirerMerchantRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.merchant_id").value(acquirerMerchantResponse.merchantId.toString()))
                .andExpect(jsonPath("$.acquirer").value(acquirerMerchantResponse.acquirer))
                .andExpect(jsonPath("$.code").value(acquirerMerchantResponse.code))

            verify(exactly = 1) { createAcquirerMerchantPortIn.execute(preAcquirerMerchant, Optional.empty()) }
            verify(exactly = 1) { findAcquirerMerchantPortIn.find(aMerchantId, "GPS") }
            verify(exactly = 1) { acquirerProvider.provideFor("GPS") }
            verify(exactly = 1) { toPreAcquirerMapper.map(acquirerMerchantRequest, aMerchantId) }
            verify(exactly = 1) { mapper.mapFrom(acquirerMerchantSaved) }
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
