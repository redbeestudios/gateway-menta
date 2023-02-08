package com.menta.api.customers.customer.application.usecase

import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerId
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Pagination
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.customerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class FindCustomerFilterByUseCaseSpec : FeatureSpec({

    val repository = mockk<CustomerRepositoryOutPort>()
    val useCase = FindCustomerByFilterUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find customer filter by") {
        val pagination = Pagination(page = 0, size = 10)
        val pageable = PageRequest.of(0, 10)
        val count = 1L

        scenario("filter by id customer found") {
            val customerQuery = CustomerQuery(id = aCustomerId, country = null, status = null, createDate = null)
            val customer = aCustomerCreated
            val result = PageImpl(listOf(customer), pageable, count)

            every { repository.findBy(customerQuery, pageable) } returns result

            useCase.execute(customerQuery, pagination) shouldBeRight result

            verify(exactly = 1) { repository.findBy(customerQuery, pageable) }
        }
        scenario("filter by status customer found") {
            val customerQuery = CustomerQuery(id = null, country = null, status = Status.ACTIVE, createDate = null)
            val customer = aCustomerCreated
            val result = PageImpl(listOf(customer), pageable, count)

            every { repository.findBy(customerQuery, pageable) } returns result

            useCase.execute(customerQuery, pagination) shouldBeRight result

            verify(exactly = 1) { repository.findBy(customerQuery, pageable) }
        }
        scenario("filter by country customer found") {
            val customerQuery = CustomerQuery(id = null, country = Country.ARG, status = null, createDate = null)
            val customer = aCustomerCreated
            val result = PageImpl(listOf(customer), pageable, count)

            every { repository.findBy(customerQuery, pageable) } returns result

            useCase.execute(customerQuery, pagination) shouldBeRight result

            verify(exactly = 1) { repository.findBy(customerQuery, pageable) }
        }
        scenario("filter by id customer NOT found") {
            val customerQuery = CustomerQuery(id = aCustomerId, country = null, status = null, createDate = null)
            val result = PageImpl(listOf<Customer>(), pageable, count)
            val error = customerNotFound(customerQuery)

            every { repository.findBy(customerQuery, pageable) } returns result

            useCase.execute(customerQuery, pagination) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(customerQuery, pageable) }
        }
    }
})
