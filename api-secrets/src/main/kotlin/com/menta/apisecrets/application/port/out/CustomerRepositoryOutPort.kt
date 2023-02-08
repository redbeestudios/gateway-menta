package com.menta.apisecrets.application.port.out

import arrow.core.Either
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.shared.error.model.ApplicationError
import java.util.UUID

interface CustomerRepositoryOutPort {

    fun findBy(id: UUID): Either<ApplicationError, Customer>
}
