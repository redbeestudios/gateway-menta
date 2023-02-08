package com.menta.api.customers.customer.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import com.menta.api.customers.aBusinessNameUpdateRequest
import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerDeleted
import com.menta.api.customers.aCustomerId
import com.menta.api.customers.aCustomerRequest
import com.menta.api.customers.aCustomerResponse
import com.menta.api.customers.aPreCustomer
import com.menta.api.customers.anApiErrorResponse
import com.menta.api.customers.anUpdateRequest
import com.menta.api.customers.anUpdatedCustomerResponse
import com.menta.api.customers.customer.adapter.`in`.controller.CustomerController
import com.menta.api.customers.customer.adapter.`in`.model.CustomerResponse
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToCustomerResponseMapper
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToCustomerUpdater
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToPreCustomerMapper
import com.menta.api.customers.customer.application.port.`in`.CreateCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.DeleteCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.FindCustomerByFilterPortIn
import com.menta.api.customers.customer.application.port.`in`.FindCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.UpdateCustomerPortIn
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Pagination
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.shared.error.ErrorHandler
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.customerNotFound
import com.menta.api.customers.shared.error.providers.CurrentResourceProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.hateoas.PagedModel
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class CustomerControllerSpec : FeatureSpec({
    val findCustomerPortIn = mockk<FindCustomerPortIn>()
    val createCustomerPortIn = mockk<CreateCustomerPortIn>()
    val deleteCustomerPortIn = mockk<DeleteCustomerPortIn>()
    val updateCustomerPortIn = mockk<UpdateCustomerPortIn>()
    val findCustomerByFilter = mockk<FindCustomerByFilterPortIn>()
    val toCustomerResponseMapper = mockk<ToCustomerResponseMapper>()
    val customerUpdater = mockk<ToCustomerUpdater>()

    val errorProvider = mockk<ErrorResponseProvider>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()

    val controller = CustomerController(
        findCustomer = findCustomerPortIn,
        createCustomer = createCustomerPortIn,
        deleteCustomer = deleteCustomerPortIn,
        updateCustomer = updateCustomerPortIn,
        findCustomerByFilter = findCustomerByFilter,
        responseMapper = toCustomerResponseMapper,
        customerMapper = ToPreCustomerMapper(),
        customerUpdater = customerUpdater
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

    feature("get customer by customerId") {

        scenario("customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val customerResponse = aCustomerResponse

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId"
            every { findCustomerPortIn.execute(customerId) } returns aCustomerCreated.right()
            every { toCustomerResponseMapper.map(aCustomerCreated) } returns customerResponse

            mockMvc
                .perform(get("/private/customers/$customerId"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(customerResponse.id.toString()))
                .andExpect(jsonPath("$.country").value(customerResponse.country.name))
                .andExpect(jsonPath("$.legal_type").value(customerResponse.legalType.toString()))
                .andExpect(jsonPath("$.business_name").value(customerResponse.businessName))
                .andExpect(jsonPath("$.fantasy_name").value(customerResponse.fantasyName))
                .andExpect(jsonPath("$.tax.type").value(customerResponse.tax.type))
                .andExpect(jsonPath("$.tax.id").value(customerResponse.tax.id))
                .andExpect(jsonPath("$.activity").value(customerResponse.activity))
                .andExpect(jsonPath("$.email").value(customerResponse.email))
                .andExpect(jsonPath("$.phone").value(customerResponse.phone))
                .andExpect(jsonPath("$.address.state").value(customerResponse.address.state))
                .andExpect(jsonPath("$.address.city").value(customerResponse.address.city))
                .andExpect(jsonPath("$.address.zip").value(customerResponse.address.zip))
                .andExpect(jsonPath("$.address.street").value(customerResponse.address.street))
                .andExpect(jsonPath("$.address.number").value(customerResponse.address.number))
                .andExpect(jsonPath("$.status").value(customerResponse.status.name))
                .andExpect(jsonPath("$.representative.representative_id.type").value(customerResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.representative.representative_id.number").value(customerResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.representative.birth_date").value(customerResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.representative.name").value(customerResponse.representative!!.name))
                .andExpect(jsonPath("$.representative.surname").value(customerResponse.representative!!.surname))
                .andExpect(jsonPath("$.business_owner.owner_id.type").value(customerResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.business_owner.owner_id.number").value(customerResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.business_owner.birth_date").value(customerResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.business_owner.name").value(customerResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.business_owner.surname").value(customerResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(customerResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.settlement_condition.settlement").value(customerResponse.settlementCondition?.settlement))
                .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(customerResponse.settlementCondition?.transactionFee))

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
            verify(exactly = 1) { toCustomerResponseMapper.map(aCustomerCreated) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val error = customerNotFound(customerId)

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId"
            every { findCustomerPortIn.execute(customerId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/customers/$customerId"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(get("/private/customers/$customerId")).andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/$customerId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
        }
    }

    feature("get customers by filter by") {

        val pagination = Pagination(page = 0, size = 10)

        scenario("filter by id customer found") {
            val uri = "/private/customers?id=$aCustomerId"
            val customerQuery = CustomerQuery(id = aCustomerId, country = null, status = null, createDate = null)

            val pageCustomer = PageImpl(listOf(aCustomerCreated))
            val result: PagedModel<CustomerResponse> = PagedModel.of(
                listOf(aCustomerResponse),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )

            every { httpServletRequest.requestURI } returns uri
            every { findCustomerByFilter.execute(customerQuery, pagination) } returns pageCustomer.right()
            every { toCustomerResponseMapper.map(pageCustomer) } returns result

            with (aCustomerCreated) {
                mockMvc
                    .perform(get(uri))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].country").value(country.toString()))
                    .andExpect(jsonPath("$.content[0].legal_type").value(legalType.toString()))
                    .andExpect(jsonPath("$.content[0].business_name").value(businessName))
                    .andExpect(jsonPath("$.content[0].fantasy_name").value(fantasyName))
                    .andExpect(jsonPath("$.content[0].tax.type").value(tax.type))
                    .andExpect(jsonPath("$.content[0].tax.id").value(tax.id))
                    .andExpect(jsonPath("$.content[0].activity").value(activity))
                    .andExpect(jsonPath("$.content[0].email").value(email))
                    .andExpect(jsonPath("$.content[0].phone").value(phone))
                    .andExpect(jsonPath("$.content[0].address.state").value(address.state))
                    .andExpect(jsonPath("$.content[0].address.city").value(address.city))
                    .andExpect(jsonPath("$.content[0].address.zip").value(address.zip))
                    .andExpect(jsonPath("$.content[0].address.street").value(address.street))
                    .andExpect(jsonPath("$.content[0].address.number").value(address.number))
                    .andExpect(jsonPath("$.content[0].status").value(status.toString()))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.type").value(representative!!.representativeId.type))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.number").value(representative!!.representativeId.number))
                    .andExpect(jsonPath("$.content[0].representative.birth_date").value(representative!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].representative.name").value(representative!!.name))
                    .andExpect(jsonPath("$.content[0].representative.surname").value(representative!!.surname))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.type").value(businessOwner!!.ownerId.type))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.number").value(businessOwner!!.ownerId.number))
                    .andExpect(jsonPath("$.content[0].business_owner.birth_date").value(businessOwner!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].business_owner.name").value(businessOwner!!.name))
                    .andExpect(jsonPath("$.content[0].business_owner.surname").value(businessOwner!!.surname))
                    .andExpect(jsonPath("$.content[0].settlement_condition.cbu_or_cvu").value(settlementCondition?.cbuOrCvu))
                    .andExpect(jsonPath("$.content[0].settlement_condition.settlement").value(settlementCondition?.settlement))
                    .andExpect(jsonPath("$.content[0].settlement_condition.transaction_fee").value(settlementCondition?.transactionFee))
            }
            
            verify(exactly = 1) { findCustomerByFilter.execute(customerQuery, pagination) }
            verify(exactly = 1) { toCustomerResponseMapper.map(pageCustomer) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
        scenario("filter by country customer found") {
            
            val uri = "/private/customers?country=${aCustomerCreated.country}"
            val customerQuery = CustomerQuery(id = null, country = Country.ARG, status = null, createDate = null)

            val pageCustomer = PageImpl(listOf(aCustomerCreated))
            val result: PagedModel<CustomerResponse> = PagedModel.of(
                listOf(aCustomerResponse),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )

            every { httpServletRequest.requestURI } returns uri
            every { findCustomerByFilter.execute(customerQuery, pagination) } returns pageCustomer.right()
            every { toCustomerResponseMapper.map(pageCustomer) } returns result

            with (aCustomerCreated) {
                mockMvc
                    .perform(get(uri))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].country").value(country.toString()))
                    .andExpect(jsonPath("$.content[0].legal_type").value(legalType.toString()))
                    .andExpect(jsonPath("$.content[0].business_name").value(businessName))
                    .andExpect(jsonPath("$.content[0].fantasy_name").value(fantasyName))
                    .andExpect(jsonPath("$.content[0].tax.type").value(tax.type))
                    .andExpect(jsonPath("$.content[0].tax.id").value(tax.id))
                    .andExpect(jsonPath("$.content[0].activity").value(activity))
                    .andExpect(jsonPath("$.content[0].email").value(email))
                    .andExpect(jsonPath("$.content[0].phone").value(phone))
                    .andExpect(jsonPath("$.content[0].address.state").value(address.state))
                    .andExpect(jsonPath("$.content[0].address.city").value(address.city))
                    .andExpect(jsonPath("$.content[0].address.zip").value(address.zip))
                    .andExpect(jsonPath("$.content[0].address.street").value(address.street))
                    .andExpect(jsonPath("$.content[0].address.number").value(address.number))
                    .andExpect(jsonPath("$.content[0].status").value(status.toString()))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.type").value(representative!!.representativeId.type))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.number").value(representative!!.representativeId.number))
                    .andExpect(jsonPath("$.content[0].representative.birth_date").value(representative!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].representative.name").value(representative!!.name))
                    .andExpect(jsonPath("$.content[0].representative.surname").value(representative!!.surname))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.type").value(businessOwner!!.ownerId.type))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.number").value(businessOwner!!.ownerId.number))
                    .andExpect(jsonPath("$.content[0].business_owner.birth_date").value(businessOwner!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].business_owner.name").value(businessOwner!!.name))
                    .andExpect(jsonPath("$.content[0].business_owner.surname").value(businessOwner!!.surname))
                    .andExpect(jsonPath("$.content[0].settlement_condition.cbu_or_cvu").value(settlementCondition?.cbuOrCvu))
                    .andExpect(jsonPath("$.content[0].settlement_condition.settlement").value(settlementCondition?.settlement))
                    .andExpect(jsonPath("$.content[0].settlement_condition.transaction_fee").value(settlementCondition?.transactionFee))
            }
            
            verify(exactly = 1) { findCustomerByFilter.execute(customerQuery, pagination) }
            verify(exactly = 1) { toCustomerResponseMapper.map(pageCustomer) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
        scenario("filter by status customer found") {
            
            val uri = "/private/customers?status=${aCustomerCreated.status}"
            val customerQuery = CustomerQuery(id = null, country = null, status = Status.ACTIVE, createDate = null)

            val pageCustomer = PageImpl(listOf(aCustomerCreated))
            val result: PagedModel<CustomerResponse> = PagedModel.of(
                listOf(aCustomerResponse),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )

            every { httpServletRequest.requestURI } returns uri
            every { findCustomerByFilter.execute(customerQuery, pagination) } returns pageCustomer.right()
            every { toCustomerResponseMapper.map(pageCustomer) } returns result

            with (aCustomerCreated) {
                mockMvc
                    .perform(get(uri))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].country").value(country.toString()))
                    .andExpect(jsonPath("$.content[0].legal_type").value(legalType.toString()))
                    .andExpect(jsonPath("$.content[0].business_name").value(businessName))
                    .andExpect(jsonPath("$.content[0].fantasy_name").value(fantasyName))
                    .andExpect(jsonPath("$.content[0].tax.type").value(tax.type))
                    .andExpect(jsonPath("$.content[0].tax.id").value(tax.id))
                    .andExpect(jsonPath("$.content[0].activity").value(activity))
                    .andExpect(jsonPath("$.content[0].email").value(email))
                    .andExpect(jsonPath("$.content[0].phone").value(phone))
                    .andExpect(jsonPath("$.content[0].address.state").value(address.state))
                    .andExpect(jsonPath("$.content[0].address.city").value(address.city))
                    .andExpect(jsonPath("$.content[0].address.zip").value(address.zip))
                    .andExpect(jsonPath("$.content[0].address.street").value(address.street))
                    .andExpect(jsonPath("$.content[0].address.number").value(address.number))
                    .andExpect(jsonPath("$.content[0].status").value(status.toString()))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.type").value(representative!!.representativeId.type))
                    .andExpect(jsonPath("$.content[0].representative.representative_id.number").value(representative!!.representativeId.number))
                    .andExpect(jsonPath("$.content[0].representative.birth_date").value(representative!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].representative.name").value(representative!!.name))
                    .andExpect(jsonPath("$.content[0].representative.surname").value(representative!!.surname))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.type").value(businessOwner!!.ownerId.type))
                    .andExpect(jsonPath("$.content[0].business_owner.owner_id.number").value(businessOwner!!.ownerId.number))
                    .andExpect(jsonPath("$.content[0].business_owner.birth_date").value(businessOwner!!.birthDate.toString()))
                    .andExpect(jsonPath("$.content[0].business_owner.name").value(businessOwner!!.name))
                    .andExpect(jsonPath("$.content[0].business_owner.surname").value(businessOwner!!.surname))
                    .andExpect(jsonPath("$.content[0].settlement_condition.cbu_or_cvu").value(settlementCondition?.cbuOrCvu))
                    .andExpect(jsonPath("$.content[0].settlement_condition.settlement").value(settlementCondition?.settlement))
                    .andExpect(jsonPath("$.content[0].settlement_condition.transaction_fee").value(settlementCondition?.transactionFee))
            }
            
            verify(exactly = 1) { findCustomerByFilter.execute(customerQuery, pagination) }
            verify(exactly = 1) { toCustomerResponseMapper.map(pageCustomer) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }
        scenario("filter by id, country and status customer NOT FOUND") {
            
            val uri = "/private/customers?id=$aCustomerId&country=${aCustomerCreated.country}&status=${aCustomerCreated.status}"
            val customerQuery = CustomerQuery(id = aCustomerId, country = Country.ARG, status = Status.ACTIVE, createDate = null)
            val error = customerNotFound(customerQuery)

            every { httpServletRequest.requestURI } returns uri
            every { findCustomerByFilter.execute(customerQuery, pagination) } returns error.left()
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(get(uri))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(uri))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(
                    jsonPath("errors[0].message")
                        .value(
                            "customer with id: $aCustomerId, " +
                                "status: ${aCustomerCreated.status}, " +
                                "country: ${aCustomerCreated.country} not found."
                        )
                )

            verify(exactly = 1) { findCustomerByFilter.execute(customerQuery, pagination) }
            verify(exactly = 0) { toCustomerResponseMapper.map(any<Page<Customer>>()) }
        }
    }

    feature("create new customer") {

        scenario("with customer valid") {
            val customerRequest = aCustomerRequest()
            val customerResponse = aCustomerResponse

            every { httpServletRequest.requestURI } returns "/private/customers/"
            every {
                findCustomerPortIn.findByUnivocity(
                    aPreCustomer.tax.type,
                    aPreCustomer.tax.id
                )
            } returns Optional.empty()
            every { createCustomerPortIn.execute(aPreCustomer, Optional.empty()) } returns aCustomerCreated.right()
            every { toCustomerResponseMapper.map(aCustomerCreated) } returns customerResponse

            mockMvc
                .perform(
                    post("/private/customers/")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest))
                )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").value(customerResponse.id.toString()))
                .andExpect(jsonPath("$.country").value(customerResponse.country.name))
                .andExpect(jsonPath("$.legal_type").value(customerResponse.legalType.name))
                .andExpect(jsonPath("$.business_name").value(customerResponse.businessName))
                .andExpect(jsonPath("$.fantasy_name").value(customerResponse.fantasyName))
                .andExpect(jsonPath("$.tax.type").value(customerResponse.tax.type))
                .andExpect(jsonPath("$.tax.id").value(customerResponse.tax.id))
                .andExpect(jsonPath("$.activity").value(customerResponse.activity))
                .andExpect(jsonPath("$.email").value(customerResponse.email))
                .andExpect(jsonPath("$.phone").value(customerResponse.phone))
                .andExpect(jsonPath("$.address.state").value(customerResponse.address.state))
                .andExpect(jsonPath("$.address.city").value(customerResponse.address.city))
                .andExpect(jsonPath("$.address.zip").value(customerResponse.address.zip))
                .andExpect(jsonPath("$.address.street").value(customerResponse.address.street))
                .andExpect(jsonPath("$.address.number").value(customerResponse.address.number))
                .andExpect(jsonPath("$.status").value(customerResponse.status.name))
                .andExpect(jsonPath("$.representative.representative_id.type").value(customerResponse.representative!!.representativeId.type))
                .andExpect(jsonPath("$.representative.representative_id.number").value(customerResponse.representative!!.representativeId.number))
                .andExpect(jsonPath("$.representative.birth_date").value(customerResponse.representative!!.birthDate.toString()))
                .andExpect(jsonPath("$.representative.name").value(customerResponse.representative!!.name))
                .andExpect(jsonPath("$.representative.surname").value(customerResponse.representative!!.surname))
                .andExpect(jsonPath("$.business_owner.owner_id.type").value(customerResponse.businessOwner!!.ownerId.type))
                .andExpect(jsonPath("$.business_owner.owner_id.number").value(customerResponse.businessOwner!!.ownerId.number))
                .andExpect(jsonPath("$.business_owner.birth_date").value(customerResponse.businessOwner!!.birthDate.toString()))
                .andExpect(jsonPath("$.business_owner.name").value(customerResponse.businessOwner!!.name))
                .andExpect(jsonPath("$.business_owner.surname").value(customerResponse.businessOwner!!.surname))
                .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(customerResponse.settlementCondition?.cbuOrCvu))
                .andExpect(jsonPath("$.settlement_condition.settlement").value(customerResponse.settlementCondition?.settlement))
                .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(customerResponse.settlementCondition?.transactionFee))

            verify(exactly = 1) { createCustomerPortIn.execute(aPreCustomer, Optional.empty()) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("with business name empty return BAD REQUEST") {
            val customerRequest = aCustomerRequest()

            every { httpServletRequest.requestURI } returns "/private/customers/"

            mockMvc
                .perform(
                    post("/private/customers/")
                        .contentType(APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(customerRequest)
                                .replace("business_name", "")
                        )
                )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }

        scenario("with invalid country return BAD REQUEST") {
            val customerRequest = aCustomerRequest()

            every { httpServletRequest.requestURI } returns "/private/customers/"

            mockMvc
                .perform(
                    post("/private/customers/")
                        .contentType(APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(customerRequest)
                                .replace("country", "INVALID_COUNTRY")
                        )
                )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }

        scenario("with invalid legal type return BAD REQUEST") {
            val customerRequest = aCustomerRequest()

            every { httpServletRequest.requestURI } returns "/private/customers/"

            mockMvc
                .perform(
                    post("/private/customers/")
                        .contentType(APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(customerRequest)
                                .replace("legal_type", "INVALID_LEGAL_TYPE")
                        )
                )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())
        }

        scenario("when customer exists") {
            val customerRequest = aCustomerRequest()
            val response = anApiErrorResponse()

            every { httpServletRequest.requestURI } returns "/private/customers/"
            every { findCustomerPortIn.findByUnivocity(aPreCustomer.tax.type, aPreCustomer.tax.id) } returns Optional.of(
                aCustomerCreated
            )
            every {
                createCustomerPortIn.execute(
                    aPreCustomer,
                    Optional.of(aCustomerCreated)
                )
            } returns ApplicationError.customerExists().left()
            every { errorProvider.provideFor(ApplicationError.customerExists()) } returns response

            mockMvc
                .perform(
                    post("/private/customers/")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest))
                )
                .andExpect(status().isUnprocessableEntity)

            verify(exactly = 1) { createCustomerPortIn.execute(aPreCustomer, Optional.of(aCustomerCreated)) }
            verify(exactly = 1) { findCustomerPortIn.findByUnivocity(aPreCustomer.tax.type, aPreCustomer.tax.id) }
        }
    }

    feature("delete customer by customerId") {

        scenario("with customer NOT deleted") {
            val customerId = aCustomerId
            val customerDeleted = aCustomerDeleted()

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId"
            every { findCustomerPortIn.execute(customerId) } returns aCustomerCreated.right()
            every { deleteCustomerPortIn.execute(aCustomerCreated) } returns customerDeleted.right()

            mockMvc
                .perform(delete("/private/customers/$customerId"))
                .andExpect(status().isNoContent)

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
            verify(exactly = 1) { deleteCustomerPortIn.execute(aCustomerCreated) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
        }

        scenario("customer NOT found") {
            val customerId = aCustomerId
            val error = customerNotFound(customerId)

            every { httpServletRequest.requestURI } returns "/private/customers/$customerId"
            every { findCustomerPortIn.execute(customerId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/customers/$customerId"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(delete("/private/customers/$customerId"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/$customerId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findCustomerPortIn.execute(customerId) }
            verify(exactly = 0) { deleteCustomerPortIn.execute(any()) }
        }
    }

    feature("update customer") {

        scenario("customer NOT found") {

            val error = customerNotFound(aCustomerId)

            every { httpServletRequest.requestURI } returns "/private/customers/$aCustomerId"
            every { findCustomerPortIn.execute(aCustomerId) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/customers/$aCustomerId"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(
                    put("/private/customers/$aCustomerId")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anUpdateRequest))
                )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/customers/$aCustomerId"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findCustomerPortIn.execute(aCustomerId) }
            verify(exactly = 0) { deleteCustomerPortIn.execute(any()) }
        }

        scenario("customer found") {

            val aCustomer = aCustomerCreated.copy(
                businessName = aBusinessNameUpdateRequest.businessName!!,
                fantasyName = aBusinessNameUpdateRequest.fantasyName!!
            )

            val now = OffsetDateTime.now()

            val anUpdatedCustomer = aCustomer.copy(
                updateDate = now
            )

            val aResponse = anUpdatedCustomerResponse(now)

            every { httpServletRequest.requestURI } returns "/private/customers/$aCustomerId"
            every { findCustomerPortIn.execute(aCustomerId) } returns aCustomerCreated.right()
            every { customerUpdater.applyChanges(aCustomerCreated, aBusinessNameUpdateRequest) } returns aCustomer
            every { updateCustomerPortIn.execute(aCustomer) } returns anUpdatedCustomer.right()
            every { toCustomerResponseMapper.map(anUpdatedCustomer) } returns aResponse

            with(aResponse) {
                mockMvc
                    .perform(
                        put("/private/customers/$aCustomerId")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(aBusinessNameUpdateRequest))
                    )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.country").value(country.name))
                    .andExpect(jsonPath("$.legal_type").value(legalType.name))
                    .andExpect(jsonPath("$.business_name").value(businessName))
                    .andExpect(jsonPath("$.fantasy_name").value(fantasyName))
                    .andExpect(jsonPath("$.tax.type").value(tax.type))
                    .andExpect(jsonPath("$.tax.id").value(tax.id))
                    .andExpect(jsonPath("$.activity").value(activity))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.phone").value(phone))
                    .andExpect(jsonPath("$.address.state").value(address.state))
                    .andExpect(jsonPath("$.address.city").value(address.city))
                    .andExpect(jsonPath("$.address.zip").value(address.zip))
                    .andExpect(jsonPath("$.address.street").value(address.street))
                    .andExpect(jsonPath("$.address.number").value(address.number))
                    .andExpect(jsonPath("$.status").value(status.name))
                    .andExpect(jsonPath("$.representative.representative_id.type").value(representative!!.representativeId.type))
                    .andExpect(jsonPath("$.representative.representative_id.number").value(representative!!.representativeId.number))
                    .andExpect(jsonPath("$.representative.birth_date").value(representative!!.birthDate.toString()))
                    .andExpect(jsonPath("$.representative.name").value(representative!!.name))
                    .andExpect(jsonPath("$.representative.surname").value(representative!!.surname))
                    .andExpect(jsonPath("$.business_owner.owner_id.type").value(businessOwner!!.ownerId.type))
                    .andExpect(jsonPath("$.business_owner.owner_id.number").value(businessOwner!!.ownerId.number))
                    .andExpect(jsonPath("$.business_owner.birth_date").value(businessOwner!!.birthDate.toString()))
                    .andExpect(jsonPath("$.business_owner.name").value(businessOwner!!.name))
                    .andExpect(jsonPath("$.business_owner.surname").value(businessOwner!!.surname))
                    .andExpect(jsonPath("$.settlement_condition.cbu_or_cvu").value(settlementCondition?.cbuOrCvu))
                    .andExpect(jsonPath("$.settlement_condition.settlement").value(settlementCondition?.settlement))
                    .andExpect(jsonPath("$.settlement_condition.transaction_fee").value(settlementCondition?.transactionFee))
            }

            verify(exactly = 1) { findCustomerPortIn.execute(aCustomerId) }
            verify(exactly = 1) { updateCustomerPortIn.execute(aCustomer) }
            verify(exactly = 0) { errorProvider.provideFor(any()) }
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
