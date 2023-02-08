package com.menta.api.merchants.acquirer.adapter.`in`.model.mapper

import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantRequest
import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantUpdateRequest
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToPreAcquirerMerchantMapper {

    fun map(acquirerMerchantRequest: AcquirerMerchantRequest, merchantId: UUID): PreAcquirerMerchant =
        with(acquirerMerchantRequest) {
            PreAcquirerMerchant(
                merchantId = merchantId,
                acquirerId = acquirerId,
                code = code
            )
        }

    fun map(acquirerMerchantUpdateRequest: AcquirerMerchantUpdateRequest, merchantId: UUID): PreAcquirerMerchant =
        with(acquirerMerchantUpdateRequest) {
            PreAcquirerMerchant(
                merchantId = merchantId,
                acquirerId = acquirerId,
                code = code
            )
        }
}
