package com.menta.apiacquirers.application.port.out

import arrow.core.Either
import com.menta.apiacquirers.domain.Customer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import java.util.UUID

interface FindCustomerPortOut {
    fun findBy(id: UUID): Either<ApplicationError, Customer>
}
