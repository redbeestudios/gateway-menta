package com.menta.api.feenicia.application.port.`in`

import com.menta.api.feenicia.domain.FeeniciaMerchant
import java.util.UUID

interface FindFeeniciaMerchantInPort {
    fun execute(merchantId: UUID): FeeniciaMerchant
}
