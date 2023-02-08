package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import com.menta.api.taxesEntities.application.port.`in`.FindTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.either.rightIfPresent
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindMerchantUseCase(
    private val merchantRepository: TaxMerchantRepositoryOutPort
) : FindTaxMerchantPortIn {

    override fun execute(merchantId: UUID): Either<ApplicationError, TaxMerchant> =
        findBy(merchantId)
            .shouldBePresent(merchantId)

    private fun findBy(merchantId: UUID) =
        merchantRepository.findBy(merchantId)

    private fun Optional<TaxMerchant>.shouldBePresent(merchantId: UUID) =
        rightIfPresent(error = ApplicationError.notFound(merchantId))
            .logEither(
                { error("tax merchant not found") },
                { info("tax merchant found: {}", it) }
            )

    companion object : CompanionLogger()
}
