package com.menta.api.merchants.application.usecase

import arrow.core.Either
import com.menta.api.merchants.application.port.`in`.FindMerchantPortIn
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.utils.either.rightIfPresent
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindMerchantUseCase(
    private val merchantRepository: MerchantRepositoryOutPort
) : FindMerchantPortIn {

    override fun execute(merchantId: UUID): Either<ApplicationError, Merchant> =
        findBy(merchantId)
            .shouldBePresent(merchantId)

    private fun findBy(merchantId: UUID) =
        merchantRepository.findBy(merchantId)

    override fun findByUnivocity(taxType: String, taxId: String): Optional<Merchant> =
        merchantRepository.findBy(taxType, taxId)
            .log { info("merchant found {}", it) }

    private fun Optional<Merchant>.shouldBePresent(merchantId: UUID) =
        rightIfPresent(error = ApplicationError.merchantNotFound(merchantId))
            .logEither(
                { error("merchant {} not found", merchantId) },
                { info("merchant found: {}", it) }
            )

    companion object : CompanionLogger()
}
