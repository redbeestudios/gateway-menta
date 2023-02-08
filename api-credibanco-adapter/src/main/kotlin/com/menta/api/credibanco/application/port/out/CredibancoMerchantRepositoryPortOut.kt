package com.menta.api.credibanco.application.port.out

import com.menta.api.credibanco.domain.CredibancoMerchant
import java.util.UUID

interface CredibancoMerchantRepositoryPortOut {
    fun findBy(merchantId: UUID): CredibancoMerchant
}
