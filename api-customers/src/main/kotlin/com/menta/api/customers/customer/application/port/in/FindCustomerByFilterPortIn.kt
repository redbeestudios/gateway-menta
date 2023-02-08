package com.menta.api.customers.customer.application.port.`in`

import arrow.core.Either
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Pagination
import com.menta.api.customers.shared.error.model.ApplicationError
import org.springframework.data.domain.Page

interface FindCustomerByFilterPortIn {
    fun execute(customerQuery: CustomerQuery, pagination: Pagination): Either<ApplicationError, Page<Customer>>
}
