package com.menta.api.customers.customer.adapter.out

import com.menta.api.customers.customer.domain.Customer
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface CustomerDbRepository : MongoRepository<Customer, UUID> {
    fun findByIdAndDeleteDateIsNull(id: UUID): Optional<Customer>

    fun findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(id: String, type: String): Optional<Customer>
}
