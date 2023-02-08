package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerResponse
import com.menta.api.customers.customer.adapter.`in`.model.CustomerResponse
import com.menta.api.customers.customer.adapter.`in`.model.hateos.CustomerModelAssembler
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.provider.AppUrlProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel

class ToCustomerResponseMapperSpec : FeatureSpec({

    val pagedResourcesAssembler: PagedResourcesAssembler<Customer> = mockk()
    val customerModelAssembler: CustomerModelAssembler = mockk()
    val appUrlProvider: AppUrlProvider = mockk()

    val mapper = ToCustomerResponseMapper(
        pagedResourcesAssembler = pagedResourcesAssembler,
        customerModelAssembler = customerModelAssembler,
        appUrlProvider = appUrlProvider
    )

    feature("map customer to response") {
        val customer = aCustomerCreated
        val customerResponse = aCustomerResponse

        every { customerModelAssembler.toModel(customer) } returns customerResponse

        scenario("successful map from customer") {
            mapper.map(
                customer
            ) shouldBe customerResponse
        }

        scenario("successful map from page customer") {
            val pageCustomer = PageImpl(listOf(customer))
            val result: PagedModel<CustomerResponse> = PagedModel.of(
                listOf(customerResponse),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )
            val link = Link.of("localhost:8080/customers").withSelfRel()

            every { customerModelAssembler.toModel(customer) } returns customerResponse
            every { pagedResourcesAssembler.toModel(pageCustomer, customerModelAssembler, link) } returns result
            every { appUrlProvider.provide() } returns "localhost:8080/customers"

            mapper.map(
                pageCustomer
            ) shouldBe result
        }
    }
})
