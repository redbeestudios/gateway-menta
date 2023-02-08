package com.menta.api.customers.acquirer.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.customers.acquirer.application.port.`in`.UpdateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerDoesNotExists
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class UpdateAcquirerCustomerUseCase(
    private val acquirerCustomerRepository: AcquirerCustomerRepositoryOutPort,
    private val dateProvider: DateProvider
) : UpdateAcquirerCustomerPortIn {

    override fun execute(
        preAcquirerCustomer: PreAcquirerCustomer,
        existingAcquirerCustomer: Optional<AcquirerCustomer>
    ): Either<ApplicationError, AcquirerCustomer> =
        existingAcquirerCustomer.shouldExist().flatMap {
            it.copy(code = preAcquirerCustomer.code,
                    updateDate = dateProvider.provide())
                .update()
                .log { info("Acquirer customer updated") }
        }

    private fun Optional<AcquirerCustomer>.shouldExist() =
        if (this.isEmpty) {
            acquirerCustomerDoesNotExists().left()
        } else {
            this.get().right()
        }

    private fun AcquirerCustomer.update() = acquirerCustomerRepository.update(this)

    companion object : CompanionLogger()
}
