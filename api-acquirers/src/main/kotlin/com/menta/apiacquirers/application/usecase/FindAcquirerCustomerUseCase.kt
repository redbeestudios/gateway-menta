package com.menta.apiacquirers.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.apiacquirers.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.apiacquirers.application.port.out.FindAcquirerCustomerPortOut
import com.menta.apiacquirers.domain.AcquirerCustomer
import com.menta.apiacquirers.domain.provider.AcquirerProvider
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirerCustomerUseCase(
    private val findAcquirerCustomer: FindAcquirerCustomerPortOut,
    private val acquirerProvider: AcquirerProvider
) : FindAcquirerCustomerPortIn {

    override fun execute(customerId: UUID, country: String): Either<ApplicationError, AcquirerCustomer> =
        acquirerProvider.provideBy(country)
            .flatMap { doFindAcquirerCustomer(customerId, it.name) }

    private fun doFindAcquirerCustomer(customerId: UUID, acquirer: String) =
        findAcquirerCustomer.findBy(customerId, acquirer)
            .logRight { info("acquirer customer found: {}", it) }

    companion object : CompanionLogger()
}
