package com.menta.api.customers.acquirer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindAcquirerCustomerPortIn {
    fun execute(customerId: UUID, acquirerId: String): Either<ApplicationError, AcquirerCustomer>
    fun find(customerId: UUID, acquirerId: String): Optional<AcquirerCustomer>
}
