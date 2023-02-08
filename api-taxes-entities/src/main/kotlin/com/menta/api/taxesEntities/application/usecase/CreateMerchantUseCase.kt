package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateMerchantUseCase(
    private val taxMerchantRepository: TaxMerchantRepositoryOutPort
) : CreateTaxMerchantPortIn {

    override fun execute(taxMerchant: TaxMerchant): Either<ApplicationError, TaxMerchant> =
        taxMerchant.save()

    private fun TaxMerchant.save() =
        taxMerchantRepository.create(this)
            .logRight { info("Tax Merchant {} created with id {}", it.merchantId, it.id) }

    companion object : CompanionLogger()
}
