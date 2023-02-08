package com.menta.apiacquirers.application.port.`in`

import arrow.core.Either
import com.menta.apiacquirers.domain.Customer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import java.util.UUID

interface FindCustomerPortIn {
    fun execute(id: UUID): Either<ApplicationError, Customer>
}
