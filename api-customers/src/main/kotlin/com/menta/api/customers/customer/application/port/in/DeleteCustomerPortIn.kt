package com.menta.api.customers.customer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.shared.error.model.ApplicationError

interface DeleteCustomerPortIn {
    fun execute(customer: Customer): Either<ApplicationError, Customer>
}
