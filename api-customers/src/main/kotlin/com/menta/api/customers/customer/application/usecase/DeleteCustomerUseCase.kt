package com.menta.api.customers.customer.application.usecase

import arrow.core.Either
import com.menta.api.customers.customer.application.port.`in`.DeleteCustomerPortIn
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.resolver.CustomerDeletionResolver
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteCustomerUseCase(
    private val customerRepository: CustomerRepositoryOutPort,
    private val deletionResolver: CustomerDeletionResolver
) : DeleteCustomerPortIn {

    override fun execute(customer: Customer): Either<ApplicationError, Customer> =
        customer.delete().persist()
            .logRight { info("customer deleted: {}", it) }

    private fun Customer.delete() =
        deletionResolver.resolveDeletion(this)

    private fun Customer.persist() =
        customerRepository.update(this)

    companion object : CompanionLogger()
}
