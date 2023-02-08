package com.menta.api.customers.customer.application.usecase

import arrow.core.Either
import com.menta.api.customers.customer.application.port.`in`.FindCustomerPortIn
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.utils.either.rightIfPresent
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val customerRepository: CustomerRepositoryOutPort
) : FindCustomerPortIn {

    override fun execute(customerId: UUID): Either<ApplicationError, Customer> =
        findBy(customerId)
            .shouldBePresent(customerId)
            .logEither(
                { error("customer {} not found", customerId.toString()) },
                { debug("customer found: {}", it) }
            )

    private fun findBy(customerId: UUID) =
        customerRepository.findBy(customerId)

    override fun findByUnivocity(taxType: String, taxId: String): Optional<Customer> =
        customerRepository.findBy(taxType, taxId)
            .log {
                info("customer found for {} and {}, {}", taxType, taxId, if (it.isPresent) it.get() else "NONE") }

    private fun Optional<Customer>.shouldBePresent(customerId: UUID) =
        rightIfPresent(error = ApplicationError.customerNotFound(customerId))


    companion object : CompanionLogger()
}
