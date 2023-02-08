package com.menta.api.customers.customer.application.usecase

import arrow.core.Either
import com.menta.api.customers.customer.application.port.`in`.FindCustomerByFilterPortIn
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Pagination
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.customerNotFound
import com.menta.api.customers.shared.utils.either.rightIfPresent
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FindCustomerByFilterUseCase(
    private val customerRepository: CustomerRepositoryOutPort
) : FindCustomerByFilterPortIn {

    override fun execute(
        customerQuery: CustomerQuery,
        pagination: Pagination
    ): Either<ApplicationError, Page<Customer>> =
        findBy(customerQuery, pageable(pagination))
            .shouldBePresent(customerQuery)

    private fun findBy(customerQuery: CustomerQuery, pageable: Pageable) =
        customerRepository.findBy(customerQuery, pageable)
            .log { info("customer searched") }

    private fun pageable(pagination: Pagination): Pageable = PageRequest.of(pagination.page, pagination.size)

    private fun Page<Customer>.shouldBePresent(customerQuery: CustomerQuery) =
        rightIfPresent(
            error = customerNotFound(customerQuery)
        ).logEither(
            { error("customer with " + filterPresent(customerQuery) + " not found.") },
            { info("customer with " + filterPresent(customerQuery) + " found.") }
        )

    private fun filterPresent(customerQuery: CustomerQuery) =
        with(customerQuery) {
            listOfNotNull(
                id?.let { "id $id" },
                status?.let { "status: $status" },
                country?.let { "country: $country" }
            ).joinToString(", ")
        }

    companion object : CompanionLogger()
}
