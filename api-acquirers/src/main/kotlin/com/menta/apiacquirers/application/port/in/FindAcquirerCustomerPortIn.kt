package com.menta.apiacquirers.application.port.`in`

import arrow.core.Either
import com.menta.apiacquirers.domain.AcquirerCustomer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import java.util.UUID

interface FindAcquirerCustomerPortIn {
    fun execute(customerId: UUID, country: String): Either<ApplicationError, AcquirerCustomer>
}
