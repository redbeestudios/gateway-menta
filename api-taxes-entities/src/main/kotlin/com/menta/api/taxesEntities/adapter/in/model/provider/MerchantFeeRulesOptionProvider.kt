package com.menta.api.taxesEntities.adapter.`in`.model.provider

import com.menta.api.taxesEntities.application.port.`in`.FindTaxCustomerPortIn
import com.menta.api.taxesEntities.domain.FeeRule
import io.kotest.assertions.arrow.core.shouldBeRight
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MerchantFeeRulesOptionProvider(
    private val taxCustomerPortIn: FindTaxCustomerPortIn
) {

    fun provide(customerId: UUID, feeRules: List<UUID>?): List<FeeRule>? {
        val taxCustomer = taxCustomerPortIn.execute(customerId).shouldBeRight()
        val merchantFeeRulesOptions = taxCustomer.merchantFeeRulesOptions
        return if (feeRules.isNullOrEmpty()) {
            null
        } else {
            merchantFeeRulesOptions?.filter {
                feeRules.contains(it.id)
            }?.map { it }
        }
    }
}
