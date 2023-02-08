package com.menta.api.merchants.acquirer.application.usecase

import arrow.core.Either
import com.menta.api.merchants.acquirer.application.port.`in`.FindAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantNotFound
import com.menta.api.merchants.shared.utils.either.rightIfPresent
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindAcquirerMerchantUseCase(
    private val repository: AcquirerMerchantRepositoryOutPort
) : FindAcquirerMerchantPortIn {

    override fun execute(acquirer: String, merchantId: UUID): Either<ApplicationError, AcquirerMerchant> =
        findBy(acquirer, merchantId)
            .shouldBePresent(merchantId, acquirer)

    private fun findBy(acquirer: String, merchantId: UUID) =
        repository.findBy(acquirer, merchantId)
            .log { info("terminal {} for acquirer {} found: {}", merchantId, acquirer, it) }

    override fun find(merchantId: UUID, acquirerId: String): Optional<AcquirerMerchant> =
        repository.findBy(acquirerId, merchantId)
            .log { info("acquirer merchant found {}", it) }

    private fun Optional<AcquirerMerchant>.shouldBePresent(merchantId: UUID, acquirer: String) =
        rightIfPresent(error = acquirerMerchantNotFound(acquirer, merchantId))
            .logEither(
                { error("terminal {} for acquirer {} not found", merchantId, acquirer) },
                { info("terminal {} for acquirer {} found: {}", merchantId, acquirer, it) }
            )

    companion object : CompanionLogger()
}
