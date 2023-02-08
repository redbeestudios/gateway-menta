package com.menta.api.taxesEntities.adapter.out

import com.menta.api.taxesEntities.domain.TaxCustomer
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface TaxCustomerDbRepository : MongoRepository<TaxCustomer, UUID> {
    fun findByCustomerId(customerId: UUID): Optional<TaxCustomer>
}
