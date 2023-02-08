package com.menta.api.merchants.acquirer.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import com.menta.api.merchants.shared.utils.logs.benchmark
import com.menta.api.merchants.shared.utils.logs.exception
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class AcquirerMerchantRepository(
    private val dbRepository: AcquirerMerchantDbRepository
) : AcquirerMerchantRepositoryOutPort {

    override fun findBy(acquirer: String, merchantId: UUID): Optional<AcquirerMerchant> =
        log.benchmark("find acquirer merchant by id") {
            dbRepository.findByAcquirerAndMerchantId(acquirer, merchantId)
        }.log { info("searched for acquirer merchant: {}", it) }

    override fun create(acquirerMerchant: AcquirerMerchant): AcquirerMerchant =
        log.benchmark("acquirer merchant create") {
            dbRepository.insert(acquirerMerchant)
        }.log { info("acquirer merchant created: {}", it) }

    override fun update(acquirerMerchant: AcquirerMerchant): Either<ApplicationError, AcquirerMerchant> =
        try {
            log.benchmark("acquirer merchant update") {
                dbRepository.save(acquirerMerchant).right()
            }.log { info("acquirer merchant updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    companion object : CompanionLogger()
}
