package com.menta.api.merchants.domain.resolver

import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class MerchantDeletionResolver {

    fun resolveDeletion(merchant: Merchant) =
        merchant.copy(deleteDate = OffsetDateTime.now())
            .log { info("merchant deletion with date: {}", it.deleteDate) }

    companion object : CompanionLogger()
}
