package com.menta.api.taxesEntities.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.taxesEntities.adapter.out.TaxMerchantRepository.Companion.log
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import com.menta.api.taxesEntities.shared.utils.logs.benchmark
import com.menta.api.taxesEntities.shared.utils.logs.exception
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class TaxCustomerRepository(
    private val taxCustomerDbRepository: TaxCustomerDbRepository
) : TaxCustomerRepositoryOutPort {

    override fun findBy(customerId: UUID): Optional<TaxCustomer> =
        log.benchmark("find customer by id") {
            taxCustomerDbRepository.findByCustomerId(customerId)
        }.log { info("customer found: {}", it) }

    override fun create(taxCustomer: TaxCustomer): Either<ApplicationError, TaxCustomer> =
        taxCustomerDbRepository.insert(taxCustomer).right()

    override fun update(taxCustomer: TaxCustomer): Either<ApplicationError, TaxCustomer> =
        try {
            log.benchmark("tax customer update") {
                taxCustomerDbRepository.save(taxCustomer).right()
            }.log { info("tax customer updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    companion object : CompanionLogger()
}
