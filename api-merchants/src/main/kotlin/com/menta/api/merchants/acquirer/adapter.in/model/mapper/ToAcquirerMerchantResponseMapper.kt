package com.menta.api.merchants.acquirer.adapter.`in`.model.mapper

import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantResponse
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerMerchantResponseMapper {

    fun mapFrom(acquirerTerminal: AcquirerMerchant) =
        with(acquirerTerminal) {
            AcquirerMerchantResponse(
                merchantId = merchantId,
                acquirer = acquirer,
                code = code,
                createDate = createDate,
                updateDate = updateDate
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
