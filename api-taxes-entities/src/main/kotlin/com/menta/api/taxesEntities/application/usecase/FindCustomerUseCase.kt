package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import com.menta.api.taxesEntities.application.port.`in`.FindTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.either.rightIfPresent
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val customerRepository: TaxCustomerRepositoryOutPort
) : FindTaxCustomerPortIn {

    override fun execute(customerId: UUID): Either<ApplicationError, TaxCustomer> =
        findBy(customerId)
            .shouldBePresent(customerId)

    private fun findBy(customerId: UUID) =
        customerRepository.findBy(customerId)

    private fun Optional<TaxCustomer>.shouldBePresent(customerId: UUID) =
        rightIfPresent(error = ApplicationError.notFound(customerId))
            .logEither(
                { error("tax customer not found") },
                { info("tax customer found: {}", it) }
            )

    companion object : CompanionLogger()
}
