package com.menta.apiacquirers.application.port.out

import arrow.core.Either
import com.menta.apiacquirers.domain.AcquirerCustomer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import java.util.UUID

interface FindAcquirerCustomerPortOut {
    fun findBy(id: UUID, acquirer: String): Either<ApplicationError, AcquirerCustomer>
}
