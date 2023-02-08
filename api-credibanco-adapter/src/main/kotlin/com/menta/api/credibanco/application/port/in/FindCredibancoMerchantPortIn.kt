package com.menta.api.credibanco.application.port.`in`

import com.menta.api.credibanco.domain.CredibancoMerchant
import java.util.UUID

interface FindCredibancoMerchantPortIn {

    fun execute(merchantId: UUID): CredibancoMerchant
}
