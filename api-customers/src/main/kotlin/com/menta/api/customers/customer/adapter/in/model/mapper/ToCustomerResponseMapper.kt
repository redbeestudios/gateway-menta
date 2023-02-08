package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.customer.adapter.`in`.model.hateos.CustomerModelAssembler
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.provider.AppUrlProvider
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.stereotype.Component

@Component
class ToCustomerResponseMapper(
    private val pagedResourcesAssembler: PagedResourcesAssembler<Customer>,
    private val customerModelAssembler: CustomerModelAssembler,
    private val appUrlProvider: AppUrlProvider
) {

    fun map(customer: Customer) = customerModelAssembler.toModel(customer)
    fun map(customers: Page<Customer>) = pagedResourcesAssembler.toModel(
        customers, customerModelAssembler,
        Link.of(appUrlProvider.provide()).withSelfRel()
    )
}
