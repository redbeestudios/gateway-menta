package com.menta.api.merchants.acquirer.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.acquirer.application.port.`in`.UpdateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantDoesNotExists
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class UpdateAcquirerMerchantUseCase(
    private val acquirerMerchantRepository: AcquirerMerchantRepositoryOutPort,
    private val dateProvider: DateProvider
) : UpdateAcquirerMerchantPortIn {

    override fun execute(
        preAcquirerMerchant: PreAcquirerMerchant,
        existingAcquirerMerchant: Optional<AcquirerMerchant>
    ): Either<ApplicationError, AcquirerMerchant> =
        existingAcquirerMerchant.shouldExist().flatMap {
            it.copy(code = preAcquirerMerchant.code!!,
            updateDate = dateProvider.provide())
                .update()
                .log { info("Acquirer merchant updated") }
        }

    private fun Optional<AcquirerMerchant>.shouldExist() =
        if (this.isEmpty) {
            acquirerMerchantDoesNotExists().left()
        } else {
            this.get().right()
        }

    private fun AcquirerMerchant.update() = acquirerMerchantRepository.update(this)

    companion object : CompanionLogger()
}
