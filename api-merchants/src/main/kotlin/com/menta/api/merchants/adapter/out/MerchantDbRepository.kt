package com.menta.api.merchants.adapter.out

import com.menta.api.merchants.domain.Merchant
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface MerchantDbRepository : MongoRepository<Merchant, UUID> {
    fun findByIdAndDeleteDateIsNull(id: UUID): Optional<Merchant>

    fun findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(id: String, type: String): Optional<Merchant>
}
