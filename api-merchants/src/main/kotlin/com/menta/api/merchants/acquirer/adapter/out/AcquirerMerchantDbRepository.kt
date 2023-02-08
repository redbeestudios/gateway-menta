package com.menta.api.merchants.acquirer.adapter.out

import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface AcquirerMerchantDbRepository : MongoRepository<AcquirerMerchant, UUID> {
    fun findByAcquirerAndMerchantId(acquirer: String, merchantId: UUID): Optional<AcquirerMerchant>
}
