package com.menta.api.customers.customer.application.usecase

import arrow.core.Either
import com.menta.api.customers.customer.application.port.`in`.UpdateCustomerPortIn
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.resolver.CustomerUpdateResolver
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class UpdateCustomerUseCase(
    private val customerRepository: CustomerRepositoryOutPort,
    private val updateResolver: CustomerUpdateResolver
) : UpdateCustomerPortIn {

    override fun execute(customer: Customer): Either<ApplicationError, Customer> =
        customer.update().persist()
            .logRight {
                debug("customer updated: {}", it)
            }

    private fun Customer.update() =
        updateResolver.resolveUpdate(this)

    private fun Customer.persist() =
        customerRepository.update(this)

    companion object : CompanionLogger()

}
