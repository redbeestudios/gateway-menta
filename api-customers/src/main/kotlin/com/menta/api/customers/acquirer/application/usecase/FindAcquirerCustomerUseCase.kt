package com.menta.api.customers.acquirer.application.usecase

import arrow.core.Either
import com.menta.api.customers.acquirer.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import com.menta.api.customers.shared.utils.either.rightIfPresent
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindAcquirerCustomerUseCase(
    private val repository: AcquirerCustomerRepositoryOutPort
) : FindAcquirerCustomerPortIn {

    override fun execute(customerId: UUID, acquirerId: String): Either<ApplicationError, AcquirerCustomer> =
        findBy(customerId, acquirerId)
            .shouldBePresent(customerId, acquirerId)

    private fun findBy(customerId: UUID, acquirerId: String) =
        repository.findBy(customerId, acquirerId)
            .log { info("customer {} for acquirer {} searched", customerId, acquirerId, it) }

    override fun find(customerId: UUID, acquirerId: String): Optional<AcquirerCustomer> =
        repository.findBy(customerId, acquirerId)
            .log { info("acquirer customer found {}", it) }

    private fun Optional<AcquirerCustomer>.shouldBePresent(customerId: UUID, acquirerId: String) =
        rightIfPresent(error = acquirerCustomerNotFound(customerId, acquirerId))
            .logEither(
                { error("customer {} for acquirer {} not found", customerId, acquirerId) },
                { info("customer {} for acquirer {} found: {}", customerId, acquirerId, it) }
            )

    companion object : CompanionLogger()
}
