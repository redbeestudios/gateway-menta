package com.menta.api.taxesEntities.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import com.menta.api.taxesEntities.shared.utils.logs.benchmark
import com.menta.api.taxesEntities.shared.utils.logs.exception
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class TaxMerchantRepository(
    private val taxMerchantDbRepository: TaxMerchantDbRepository
) : TaxMerchantRepositoryOutPort {

    override fun findBy(merchantId: UUID): Optional<TaxMerchant> =
        log.benchmark("find merchant by id") {
            taxMerchantDbRepository.findByMerchantId(merchantId)
        }.log { info("merchant found: {}", it) }

    override fun create(merchant: TaxMerchant): Either<ApplicationError, TaxMerchant> =
        taxMerchantDbRepository.insert(merchant).right()

    override fun update(taxMerchant: TaxMerchant): Either<ApplicationError, TaxMerchant> =
        try {
            log.benchmark("tax merchant update") {
                taxMerchantDbRepository.save(taxMerchant).right()
            }.log { info("tax merchant updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    companion object : CompanionLogger()
}
