package com.menta.api.feenicia.application.port.out

import com.menta.api.feenicia.domain.FeeniciaMerchant
import java.util.UUID

interface FeeniciaMerchantClientRepository {
    fun findBy(merchantId: UUID): FeeniciaMerchant
}
