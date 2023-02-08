package com.menta.api.merchants.merchant.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.menta.api.merchants.aDeletedMerchant
import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.aMerchantCreated
import com.menta.api.merchants.aMerchantQuery
import com.menta.api.merchants.aMerchantRequest
import com.menta.api.merchants.aMerchantResponse
import com.menta.api.merchants.aPreMerchant
import com.menta.api.merchants.aUpdateRequest
import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.adapter.`in`.controller.MerchantController
import com.menta.api.merchants.adapter.`in`.model.MerchantResponse
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantResponseMapper
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantUpdater
import com.menta.api.merchants.adapter.`in`.model.mapper.ToPreMerchantMapper
import com.menta.api.merchants.application.port.`in`.CreateMerchantPortIn
import com.menta.api.merchants.application.port.`in`.DeleteMerchantPortIn
import com.menta.api.merchants.application.port.`in`.FindMerchantByFilterPortIn
import com.menta.api.merchants.application.port.`in`.FindMerchantPortIn
import com.menta.api.merchants.application.port.`in`.UpdateMerchantPortIn
import com.menta.api.merchants.domain.Pagination
import com.menta.api.merchants.domain.Status.ACTIVE
import com.menta.api.merchants.shared.error.ErrorHandler
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantNotFound
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.merchantNotFound
import com.menta.api.merchants.shared.error.providers.CurrentResourceProvider
import com.menta.api.merchants.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.merchants.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.hateoas.PagedModel
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

class MerchantControllerSpec : FeatureSpec({
    val findMerchantPortIn = mockk<FindMerchantPortIn>()
    val mapper = mockk<ToMerchantResponseMapper>()
    val createMerchantPortIn = mockk<CreateMerchantPortIn>()
    val deleteMerchantPortIn = mockk<DeleteMerchantPortIn>()
    val toPreMerchantMapper = mockk<ToPreMerchantMapper>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()
    val findMerchantByFilterPortIn: FindMerchantByFilterPortIn = mockk()
    val toMerchantUpdater: ToMerchantUpdater = mockk()
    val updateMerchantPortIn: UpdateMerchantPortIn = mockk()

    val controller = MerchantController(
        findMerchant = findMerchantPortIn,
        deleteMerchantPortIn = deleteMerchantPortIn,
        createMerchantPortIn = createMerchantPortIn,
        toResponseMapper = mapper,
        toPreMerchantMapper = toPreMerchantMapper,
        toMerchantUpdater = toMerchantUpdater,
        findMerchantByFilterPortIn = findMerchantByFilterPortIn,
        updateMerchantPortIn = updateMerchantPortIn
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

    feature("get acquirer merchant by merchantId and acquirerId") {

        scenario("acquirer merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchant = aMerchant()
            val response = aMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns merchant.right()
            every { mapper.map(merchant) } returns response

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/merchants/$merchantId"
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(response.id.toString()))

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
            verify(exactly = 1) { mapper.map(merchant) }
        }

        scenario("acquirer merchant NOT found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "GPS"
            val error = acquirerMerchantNotFound(acquirerId, merchantId)

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/merchants/$merchantId"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/private/merchants/$merchantId"
                )
            ).andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/merchants/$merchantId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
        }
    }

    feature("update merchant") {
        scenario("with put verb") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchantRequest = aMerchantRequest()
            val merchant = aMerchantCreated()
            val merchantResponse = aMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns merchant.right()
            every { toMerchantUpdater.applyChanges(merchant,merchantRequest) } returns merchant
            every { updateMerchantPortIn.execute(merchant) } returns merchant.right()
            every { mapper.map(merchant) } returns merchantResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/private/merchants/$merchantId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(merchantRequest)
                    )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(merchantResponse.id.toString()))
                .andExpect(jsonPath("$.customer_id").value(merchantResponse.customerId))
                .andExpect(jsonPath("$.country").value(merchantResponse.country.name))
                .andExpect(jsonPath("$.legal_type").value(merchantResponse.legalType.toString()))
                .andExpect(jsonPath("$.business_name").value(merchantResponse.businessName))
                .andExpect(jsonPath("$.fantasy_name").value(merchantResponse.fantasyName))
                .andExpect(jsonPath("$.representative.representative_id.type").value(merchantResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.representative.representative_id.number").value(merchantResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.representative.birth_date").value(merchantResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.representative.name").value(merchantResponse.representative!!.name))
                .andExpect(jsonPath("$.representative.surname").value(merchantResponse.representative!!.surname))
                .andExpect(jsonPath("$.business_owner.owner_id.type").value(merchantResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.business_owner.owner_id.number").value(merchantResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.business_owner.birth_date").value(merchantResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.business_owner.name").value(merchantResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.business_owner.surname").value(merchantResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.merchant_code").value(merchantResponse.merchantCode))
                .andExpect(jsonPath("$.address.state").value(merchantResponse.address.state))
                .andExpect(jsonPath("$.address.city").value(merchantResponse.address.city))
                .andExpect(jsonPath("$.address.zip").value(merchantResponse.address.zip))
                .andExpect(jsonPath("$.address.street").value(merchantResponse.address.street))
                .andExpect(jsonPath("$.address.number").value(merchantResponse.address.number))
                .andExpect(jsonPath("$.email").value(merchantResponse.email))
                .andExpect(jsonPath("$.phone").value(merchantResponse.phone))
                .andExpect(jsonPath("$.activity").value(merchantResponse.activity))
                .andExpect(jsonPath("$.tax.type").value(merchantResponse.tax.type))
                .andExpect(jsonPath("$.tax.id").value(merchantResponse.tax.id))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(merchantResponse.settlementCondition.cbuOrCvu))
                .andExpect(jsonPath("$.settlement_condition.settlement").value(merchantResponse.settlementCondition.settlement))
                .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(merchantResponse.settlementCondition.transactionFee))

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
            verify(exactly = 1) { toMerchantUpdater.applyChanges(merchant,merchantRequest) }
            verify(exactly = 1) { updateMerchantPortIn.execute(merchant) }
            verify(exactly = 1) { mapper.map(merchant) }
        }

        scenario("with patch verb") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchantRequest = aUpdateRequest()
            val merchant = aMerchantCreated()
            val merchantResponse = aMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns merchant.right()
            every { toMerchantUpdater.applyChanges(merchant,merchantRequest) } returns merchant
            every { updateMerchantPortIn.execute(merchant) } returns merchant.right()
            every { mapper.map(merchant) } returns merchantResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/private/merchants/$merchantId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(merchantRequest)
                    )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(merchantResponse.id.toString()))
                .andExpect(jsonPath("$.customer_id").value(merchantResponse.customerId))
                .andExpect(jsonPath("$.country").value(merchantResponse.country.name))
                .andExpect(jsonPath("$.legal_type").value(merchantResponse.legalType.toString()))
                .andExpect(jsonPath("$.business_name").value(merchantResponse.businessName))
                .andExpect(jsonPath("$.fantasy_name").value(merchantResponse.fantasyName))
                .andExpect(jsonPath("$.representative.representative_id.type").value(merchantResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.representative.representative_id.number").value(merchantResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.representative.birth_date").value(merchantResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.representative.name").value(merchantResponse.representative!!.name))
                .andExpect(jsonPath("$.representative.surname").value(merchantResponse.representative!!.surname))
                .andExpect(jsonPath("$.business_owner.owner_id.type").value(merchantResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.business_owner.owner_id.number").value(merchantResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.business_owner.birth_date").value(merchantResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.business_owner.name").value(merchantResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.business_owner.surname").value(merchantResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.merchant_code").value(merchantResponse.merchantCode))
                .andExpect(jsonPath("$.address.state").value(merchantResponse.address.state))
                .andExpect(jsonPath("$.address.city").value(merchantResponse.address.city))
                .andExpect(jsonPath("$.address.zip").value(merchantResponse.address.zip))
                .andExpect(jsonPath("$.address.street").value(merchantResponse.address.street))
                .andExpect(jsonPath("$.address.number").value(merchantResponse.address.number))
                .andExpect(jsonPath("$.email").value(merchantResponse.email))
                .andExpect(jsonPath("$.phone").value(merchantResponse.phone))
                .andExpect(jsonPath("$.activity").value(merchantResponse.activity))
                .andExpect(jsonPath("$.tax.type").value(merchantResponse.tax.type))
                .andExpect(jsonPath("$.tax.id").value(merchantResponse.tax.id))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(merchantResponse.settlementCondition.cbuOrCvu))
                .andExpect(jsonPath("$.settlement_condition.settlement").value(merchantResponse.settlementCondition.settlement))
                .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(merchantResponse.settlementCondition.transactionFee))

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
            verify(exactly = 1) { toMerchantUpdater.applyChanges(merchant,merchantRequest) }
            verify(exactly = 1) { updateMerchantPortIn.execute(merchant) }
            verify(exactly = 1) { mapper.map(merchant) }
        }
    }

    feature("create new merchant") {

        scenario("with merchant valid") {
            val merchantRequest = aMerchantRequest()
            val preMerchant = aPreMerchant()
            val merchantSaved = aMerchantCreated()
            val merchantResponse = aMerchantResponse()

            every { httpServletRequest.requestURI } returns "/private/merchants/"
            every { toPreMerchantMapper.mapFrom(merchantRequest) } returns preMerchant
            every {
                findMerchantPortIn.findByUnivocity(
                    preMerchant.tax.type,
                    preMerchant.tax.id
                )
            } returns Optional.empty()
            every { createMerchantPortIn.execute(preMerchant, Optional.empty()) } returns merchantSaved.right()
            every { mapper.map(merchantSaved) } returns merchantResponse

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/merchants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(merchantRequest)
                    )
            ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(merchantResponse.id.toString()))
                .andExpect(jsonPath("$.customer_id").value(merchantResponse.customerId))
                .andExpect(jsonPath("$.country").value(merchantResponse.country.name))
                .andExpect(jsonPath("$.legal_type").value(merchantResponse.legalType.toString()))
                .andExpect(jsonPath("$.business_name").value(merchantResponse.businessName))
                .andExpect(jsonPath("$.fantasy_name").value(merchantResponse.fantasyName))
                .andExpect(jsonPath("$.representative.representative_id.type").value(merchantResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.representative.representative_id.number").value(merchantResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.representative.birth_date").value(merchantResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.representative.name").value(merchantResponse.representative!!.name))
                .andExpect(jsonPath("$.representative.surname").value(merchantResponse.representative!!.surname))
                .andExpect(jsonPath("$.business_owner.owner_id.type").value(merchantResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.business_owner.owner_id.number").value(merchantResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.business_owner.birth_date").value(merchantResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.business_owner.name").value(merchantResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.business_owner.surname").value(merchantResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.merchant_code").value(merchantResponse.merchantCode))
                .andExpect(jsonPath("$.address.state").value(merchantResponse.address.state))
                .andExpect(jsonPath("$.address.city").value(merchantResponse.address.city))
                .andExpect(jsonPath("$.address.zip").value(merchantResponse.address.zip))
                .andExpect(jsonPath("$.address.street").value(merchantResponse.address.street))
                .andExpect(jsonPath("$.address.number").value(merchantResponse.address.number))
                .andExpect(jsonPath("$.email").value(merchantResponse.email))
                .andExpect(jsonPath("$.phone").value(merchantResponse.phone))
                .andExpect(jsonPath("$.activity").value(merchantResponse.activity))
                .andExpect(jsonPath("$.tax.type").value(merchantResponse.tax.type))
                .andExpect(jsonPath("$.tax.id").value(merchantResponse.tax.id))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(merchantResponse.settlementCondition.cbuOrCvu))
                .andExpect(jsonPath("$.settlement_condition.settlement").value(merchantResponse.settlementCondition.settlement))
                .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(merchantResponse.settlementCondition.transactionFee))

            verify(exactly = 1) { toPreMerchantMapper.mapFrom(merchantRequest) }
            verify(exactly = 1) { createMerchantPortIn.execute(preMerchant, Optional.empty()) }
            verify(exactly = 1) { mapper.map(merchantSaved) }
        }

        scenario("when merchant exists") {
            val merchantRequest = aMerchantRequest()
            val preMerchant = aPreMerchant()
            val merchantSaved = aMerchantCreated()

            every { httpServletRequest.requestURI } returns "/private/merchants/"
            every { toPreMerchantMapper.mapFrom(merchantRequest) } returns preMerchant
            every { findMerchantPortIn.findByUnivocity(preMerchant.tax.type, preMerchant.tax.id) } returns Optional.of(
                merchantSaved
            )
            every {
                createMerchantPortIn.execute(
                    preMerchant,
                    Optional.of(merchantSaved)
                )
            } returns ApplicationError.merchantExists().left()
            every { currentResourceProvider.provideUri() } returns "/private/merchants/"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/merchants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(merchantRequest)
                    )
            ).andExpect(status().isUnprocessableEntity)

            verify(exactly = 1) { toPreMerchantMapper.mapFrom(merchantRequest) }
            verify(exactly = 1) { createMerchantPortIn.execute(preMerchant, Optional.of(merchantSaved)) }
            verify(exactly = 1) { findMerchantPortIn.findByUnivocity(preMerchant.tax.type, preMerchant.tax.id) }
        }

        scenario("with business name empty return BAD REQUEST") {
            val merchantRequest = aMerchantRequest()

            every { httpServletRequest.requestURI } returns "/private/merchants/"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/merchants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule()).setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(merchantRequest)
                            .replace("business_name", "")
                    )
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("013"))
                .andExpect(jsonPath("errors[0].resource").value("/private/merchants/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").isNotEmpty)
        }

        scenario("with invalid country return BAD REQUEST") {
            val merchantRequest = aMerchantRequest()

            every { httpServletRequest.requestURI } returns "/private/merchants/"

            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/private/merchants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jacksonObjectMapper().registerModule(JavaTimeModule())
                            .setPropertyNamingStrategy(SNAKE_CASE)
                            .writeValueAsString(merchantRequest)
                            .replace("country", "INVALID_COUNTRY")
                    )
            ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].code").value("013"))
                .andExpect(jsonPath("errors[0].resource").value("/private/merchants/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").isNotEmpty)
        }
    }

    feature("delete merchant by merchantId") {

        scenario("with merchant NOT deleted") {
            val merchantId = aMerchantId
            val merchant = aMerchant()
            val deletedMerchant = aDeletedMerchant()

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns merchant.right()
            every { deleteMerchantPortIn.execute(merchant) } returns deletedMerchant.right()

            mockMvc.perform(
                MockMvcRequestBuilders.delete(
                    "/private/merchants/$merchantId"
                )
            ).andExpect(status().isNoContent)

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
            verify(exactly = 1) { deleteMerchantPortIn.execute(merchant) }
        }

        scenario("merchant NOT found") {
            val merchantId = aMerchantId
            val error = merchantNotFound(merchantId)

            every { httpServletRequest.requestURI } returns "/private/merchants/$merchantId"
            every { findMerchantPortIn.execute(merchantId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/merchants/$merchantId"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.delete(
                    "/private/merchants/$merchantId"
                )
            ).andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/merchants/$merchantId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findMerchantPortIn.execute(merchantId) }
            verify(exactly = 0) { deleteMerchantPortIn.execute(any()) }
        }

        scenario("Merchant found with status Active") {

            val status = ACTIVE
            val merchantResponse = aMerchantResponse()
            val pageMerchant = PageImpl(listOf(aMerchant()))
            val result: PagedModel<MerchantResponse> = PagedModel.of(
                listOf(merchantResponse),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )
            val merchantQuery = aMerchantQuery(status = status)
            val pagination = Pagination(0, 10)

            val uri = "/private/merchants?status=$status"
            every { httpServletRequest.requestURI } returns uri

            every {
                findMerchantByFilterPortIn.execute(
                    merchantQuery,
                    pagination
                )
            } returns pageMerchant.right()

            every { mapper.map(pageMerchant) } returns result

            mockMvc.perform(
                MockMvcRequestBuilders.get(uri)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].id").value(merchantResponse.id.toString()))
                .andExpect(jsonPath("$.content[0].customer_id").value(merchantResponse.customerId))
                .andExpect(jsonPath("$.content[0].country").value(merchantResponse.country.name))
                .andExpect(jsonPath("$.content[0].legal_type").value(merchantResponse.legalType.toString()))
                .andExpect(jsonPath("$.content[0].business_name").value(merchantResponse.businessName))
                .andExpect(jsonPath("$.content[0].fantasy_name").value(merchantResponse.fantasyName))
                .andExpect(jsonPath("$.content[0].representative.representative_id.type").value(merchantResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.content[0].representative.representative_id.number").value(merchantResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.content[0].representative.birth_date").value(merchantResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.content[0].representative.name").value(merchantResponse.representative!!.name))
                .andExpect(jsonPath("$.content[0].representative.surname").value(merchantResponse.representative!!.surname))
                .andExpect(jsonPath("$.content[0].business_owner.owner_id.type").value(merchantResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.content[0].business_owner.owner_id.number").value(merchantResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.content[0].business_owner.birth_date").value(merchantResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.content[0].business_owner.name").value(merchantResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.content[0].business_owner.surname").value(merchantResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.content[0].merchant_code").value(merchantResponse.merchantCode))
                .andExpect(jsonPath("$.content[0].address.state").value(merchantResponse.address.state))
                .andExpect(jsonPath("$.content[0].address.city").value(merchantResponse.address.city))
                .andExpect(jsonPath("$.content[0].address.zip").value(merchantResponse.address.zip))
                .andExpect(jsonPath("$.content[0].address.street").value(merchantResponse.address.street))
                .andExpect(jsonPath("$.content[0].address.number").value(merchantResponse.address.number))
                .andExpect(jsonPath("$.content[0].email").value(merchantResponse.email))
                .andExpect(jsonPath("$.content[0].phone").value(merchantResponse.phone))
                .andExpect(jsonPath("$.content[0].activity").value(merchantResponse.activity))
                .andExpect(jsonPath("$.content[0].tax.type").value(merchantResponse.tax.type))
                .andExpect(jsonPath("$.content[0].tax.id").value(merchantResponse.tax.id))
                .andExpect(jsonPath("$.content[0].settlement_condition.cbu_or_cvu").value(merchantResponse.settlementCondition.cbuOrCvu))
                .andExpect(jsonPath("$.content[0].settlement_condition.settlement").value(merchantResponse.settlementCondition.settlement))
                .andExpect(jsonPath("$.content[0].settlement_condition.transaction_fee").value(merchantResponse.settlementCondition.transactionFee))

            verify(exactly = 1) {
                findMerchantByFilterPortIn.execute(
                    merchantQuery,
                    pagination
                )
            }
            verify(exactly = 1) { mapper.map(pageMerchant) }
        }

        scenario("merchants NOT found with id") {

            val id = "850363d9-8a9d-4843-acf1-1c1f09be86ab"
            val uri = "/private/merchants?id=$id"
            val merchantQuery = aMerchantQuery(UUID.fromString(id))
            val pagination = Pagination(0, 10)
            val error = merchantNotFound(merchantQuery)

            every { httpServletRequest.requestURI } returns uri
            every {
                findMerchantByFilterPortIn.execute(
                    merchantQuery,
                    pagination
                )
            } returns error.left()
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc.perform(
                MockMvcRequestBuilders.get(uri)
            )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(uri))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) {
                findMerchantByFilterPortIn.execute(
                    merchantQuery,
                    pagination

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
