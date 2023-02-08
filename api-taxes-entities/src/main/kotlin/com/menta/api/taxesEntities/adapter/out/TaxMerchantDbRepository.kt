package com.menta.api.taxesEntities.adapter.out

import com.menta.api.taxesEntities.domain.TaxMerchant
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface TaxMerchantDbRepository : MongoRepository<TaxMerchant, UUID> {
    fun findByMerchantId(customerId: UUID): Optional<TaxMerchant>
}
