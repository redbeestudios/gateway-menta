package com.menta.api.customers.customer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindCustomerPortIn {
    fun execute(customerId: UUID): Either<ApplicationError, Customer>
    fun findByUnivocity(taxType: String, taxId: String): Optional<Customer>
}
