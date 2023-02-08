package com.menta.api.customers.acquirer.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.customers.acquirer.application.mapper.ToAcquirerCustomerMapper
import com.menta.api.customers.acquirer.application.port.`in`.CreateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerExists
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreateAcquirerCustomerUseCase(
    private val acquirerCustomerRepository: AcquirerCustomerRepositoryOutPort,
    private val toAcquirerCustomerMapper: ToAcquirerCustomerMapper
) : CreateAcquirerCustomerPortIn {

    override fun execute(
        preAcquirerCustomer: PreAcquirerCustomer,
        existingAcquirerCustomer: Optional<AcquirerCustomer>
    ): Either<ApplicationError, AcquirerCustomer> =
        existingAcquirerCustomer.shouldNotExist().map {
            preAcquirerCustomer
                .toAcquirerCustomer()
                .save()
                .log { info("Acquirer customer {} for {} created", it.customerId.toString(), it.acquirerId) }
        }

    private fun Optional<AcquirerCustomer>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            acquirerCustomerExists().left()
        }

    private fun PreAcquirerCustomer.toAcquirerCustomer(): AcquirerCustomer = toAcquirerCustomerMapper.map(this)

    private fun AcquirerCustomer.save() = acquirerCustomerRepository.create(this)

    companion object : CompanionLogger()
}
