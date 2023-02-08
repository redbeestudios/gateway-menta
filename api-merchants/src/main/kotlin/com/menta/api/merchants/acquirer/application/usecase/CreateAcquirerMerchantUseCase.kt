package com.menta.api.merchants.acquirer.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.acquirer.application.mapper.ToAcquirerMerchantMapper
import com.menta.api.merchants.acquirer.application.port.`in`.CreateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantExists
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreateAcquirerMerchantUseCase(
    private val acquirerMerchantRepository: AcquirerMerchantRepositoryOutPort,
    private val toAcquirerMerchantMapper: ToAcquirerMerchantMapper
) : CreateAcquirerMerchantPortIn {

    override fun execute(
        preAcquirerMerchant: PreAcquirerMerchant,
        existingAcquirerMerchant: Optional<AcquirerMerchant>
    ): Either<ApplicationError, AcquirerMerchant> =
        existingAcquirerMerchant.shouldNotExist().map {
            preAcquirerMerchant
                .toAcquirerMerchant()
                .save()
                .log { info("Acquirer merchant {} for {} created", it.merchantId.toString(), it.acquirer) }
        }

    private fun Optional<AcquirerMerchant>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            acquirerMerchantExists().left()
        }

    private fun PreAcquirerMerchant.toAcquirerMerchant(): AcquirerMerchant = toAcquirerMerchantMapper.map(this)

    private fun AcquirerMerchant.save() = acquirerMerchantRepository.create(this)

    companion object : CompanionLogger()
}
