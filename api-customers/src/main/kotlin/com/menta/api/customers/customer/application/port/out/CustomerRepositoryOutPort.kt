package com.menta.api.customers.customer.application.port.out

import arrow.core.Either
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface CustomerRepositoryOutPort {
    fun findBy(customerId: UUID): Optional<Customer>
    fun findBy(taxType: String, taxId: String): Optional<Customer>
    fun findBy(customerQuery: CustomerQuery, pageable: Pageable): Page<Customer>
    fun create(customer: Customer): Customer
    fun update(customer: Customer): Either<ApplicationError, Customer>
}
