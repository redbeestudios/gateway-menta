package com.menta.api.customers.acquirer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import java.util.Optional

interface CreateAcquirerCustomerPortIn {
    fun execute(preAcquirerCustomer: PreAcquirerCustomer, existingAcquirerCustomer: Optional<AcquirerCustomer>): Either<ApplicationError, AcquirerCustomer>
}
