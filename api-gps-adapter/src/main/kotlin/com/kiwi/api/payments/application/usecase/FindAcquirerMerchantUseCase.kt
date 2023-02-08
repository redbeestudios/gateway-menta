package com.kiwi.api.payments.application.usecase

import com.kiwi.api.payments.application.port.`in`.FindAcquirerMerchantInPort
import com.kiwi.api.payments.application.port.out.AcquirerMerchantRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerMerchant
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirerMerchantUseCase(
    private val repository: AcquirerMerchantRepositoryOutPort
) : FindAcquirerMerchantInPort {

    override fun execute(merchantId: UUID): AcquirerMerchant =
        log.benchmark("Find Acquirer Merchant with id: $merchantId") {
            repository.findBy(merchantId)
                .log { info("Acquirer Merchant found: {}", it) }
        }

    companion object : CompanionLogger()
}
