package com.kiwi.api.payments.application.usecase

import com.kiwi.api.payments.application.port.`in`.FindAcquirerCustomerInPort
import com.kiwi.api.payments.application.port.out.AcquirerCustomerRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerCustomer
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirerCustomerUseCase(
    private val repository: AcquirerCustomerRepositoryOutPort
) : FindAcquirerCustomerInPort {

    override fun execute(customerId: UUID): AcquirerCustomer? =
        log.benchmark("Find Acquirer Customer with id: $customerId") {
            repository.findBy(customerId)
                .log { info("Acquirer Customer found: {}", it) }
        }
    companion object : CompanionLogger()
}
