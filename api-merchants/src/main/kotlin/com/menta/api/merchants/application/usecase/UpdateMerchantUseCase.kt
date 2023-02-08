package com.menta.api.merchants.application.usecase

import arrow.core.Either
import com.menta.api.merchants.application.port.`in`.UpdateMerchantPortIn
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.resolver.MerchantActualizationResolver
import com.menta.api.merchants.shared.error.model.ApplicationError
import org.springframework.stereotype.Component

@Component
class UpdateMerchantUseCase(
    private val merchantRepository: MerchantRepositoryOutPort,
    private val merchantActualization: MerchantActualizationResolver
) : UpdateMerchantPortIn {

    override fun execute(merchant: Merchant): Either<ApplicationError, Merchant> =
        merchant
            .update()
            .persist()

    private fun Merchant.update() =
        merchantActualization.resolveActualization(this)

    private fun Merchant.persist() =
        merchantRepository.update(this)
}
