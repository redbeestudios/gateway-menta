package com.menta.api.customers.acquirer.application.port.out

import arrow.core.Either
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface AcquirerCustomerRepositoryOutPort {
    fun findBy(customerId: UUID, acquirerId: String): Optional<AcquirerCustomer>
    fun create(acquirerCustomer: AcquirerCustomer): AcquirerCustomer
    fun update(acquirerCustomer: AcquirerCustomer): Either<ApplicationError, AcquirerCustomer>
}
