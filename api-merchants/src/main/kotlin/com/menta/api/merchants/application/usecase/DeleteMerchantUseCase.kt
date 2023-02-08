package com.menta.api.merchants.application.usecase

import arrow.core.Either
import com.menta.api.merchants.application.port.`in`.DeleteMerchantPortIn
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.resolver.MerchantDeletionResolver
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteMerchantUseCase(
    private val merchantRepository: MerchantRepositoryOutPort,
    private val deletionResolver: MerchantDeletionResolver
) : DeleteMerchantPortIn {

    override fun execute(merchant: Merchant): Either<ApplicationError, Merchant> =
        merchant.delete().persist()

    private fun Merchant.delete() =
        deletionResolver.resolveDeletion(this)

    private fun Merchant.persist() =
        merchantRepository.update(this)

    companion object : CompanionLogger()
}
