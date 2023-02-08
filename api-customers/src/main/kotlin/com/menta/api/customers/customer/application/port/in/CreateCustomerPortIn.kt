package com.menta.api.customers.customer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.PreCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import java.util.Optional

interface CreateCustomerPortIn {
    fun execute(preCustomer: PreCustomer, existingCustomer: Optional<Customer>): Either<ApplicationError, Customer>
}
