package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToMerchantBuilder {

    fun buildFrom(
        merchant: Merchant?,
        taxMerchant: TaxMerchant?
    ) = merchant
        ?.copy(taxCondition = taxMerchant?.taxCondition)
        .log { info("merchant built: {}", it) }

    companion object : CompanionLogger()
}
