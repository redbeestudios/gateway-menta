package com.menta.api.customers.acquirer.adapter.out

import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface AcquirerCustomerDbRepository : MongoRepository<AcquirerCustomer, UUID> {
    fun findByCustomerIdAndAcquirerId(customerId: UUID, acquirerId: String): Optional<AcquirerCustomer>
}
