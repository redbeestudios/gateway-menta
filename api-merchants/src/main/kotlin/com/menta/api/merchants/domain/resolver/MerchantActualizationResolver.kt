package com.menta.api.merchants.domain.resolver

import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import java.time.OffsetDateTime
import org.springframework.stereotype.Component

@Component
class MerchantActualizationResolver {

    fun resolveActualization(merchant: Merchant) =
        merchant.copy(updateDate = OffsetDateTime.now())
            .log { info("merchant updated with date: {}", it.updateDate) }

    companion object : CompanionLogger()
}
