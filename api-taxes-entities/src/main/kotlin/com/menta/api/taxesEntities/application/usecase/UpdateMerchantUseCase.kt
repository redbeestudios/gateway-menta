package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.taxesEntities.adapter.`in`.controller.TaxCustomerController.Companion.log
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.domain.PreTaxMerchant
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxMerchantDoesNotExists
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class UpdateMerchantUseCase(
    private val merchantRepository: TaxMerchantRepositoryOutPort
) : UpdateTaxMerchantPortIn {

    override fun execute(preTaxMerchant: PreTaxMerchant, merchantId: UUID): Either<ApplicationError, TaxMerchant> =
        with(preTaxMerchant) {
            get(merchantId).shouldExist().flatMap {
                it.copy(
                    taxCondition = taxCondition,
                    settlementCondition = settlementCondition,
                    customerId = customerId,
                    country = country,
                    feeRules = feeRules
                )
                    .update()
                    .log { info("Tax merchant updated") }
            }
        }

    private fun get(merchantId: UUID) = merchantRepository.findBy(merchantId)

    private fun TaxMerchant.update() =
        merchantRepository.update(this)

    private fun Optional<TaxMerchant>.shouldExist() =
        if (this.isEmpty) {
            taxMerchantDoesNotExists().left()
        } else {
            this.get().right()
        }
}
