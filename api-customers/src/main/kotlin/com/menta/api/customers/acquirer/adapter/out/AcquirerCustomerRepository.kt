package com.menta.api.customers.acquirer.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import com.menta.api.customers.shared.utils.logs.benchmark
import com.menta.api.customers.shared.utils.logs.exception
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class AcquirerCustomerRepository(
    private val dbRepository: AcquirerCustomerDbRepository
) : AcquirerCustomerRepositoryOutPort {

    override fun findBy(customerId: UUID, acquirerId: String): Optional<AcquirerCustomer> =
        log.benchmark("find acquirer customer by id") {
            dbRepository.findByCustomerIdAndAcquirerId(customerId, acquirerId)
        }.log { info("searched for acquirer customer: {}", it) }

    override fun create(acquirerCustomer: AcquirerCustomer): AcquirerCustomer =
        log.benchmark("acquirer customer create") {
            dbRepository.insert(acquirerCustomer)
        }.log { info("acquirer customer created: {}", it) }

    override fun update(acquirerCustomer: AcquirerCustomer): Either<ApplicationError, AcquirerCustomer> =
        try {
            log.benchmark("acquirer customer update") {
                dbRepository.save(acquirerCustomer).right()
            }.log { info("acquirer customer updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    companion object : CompanionLogger()
}
