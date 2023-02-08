package com.menta.bff.devices.login.entities.customer.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindCustomerPortOut {
    fun findBy(id: UUID): Either<ApplicationError, Customer>
}
