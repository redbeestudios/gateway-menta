package com.menta.api.feenicia.application.usecase

import com.menta.api.feenicia.application.port.`in`.FindFeeniciaMerchantInPort
import com.menta.api.feenicia.application.port.out.FeeniciaMerchantClientRepository
import com.menta.api.feenicia.domain.FeeniciaMerchant
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import com.menta.api.feenicia.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindFeeniciaMerchantUseCase(
    private val repository: FeeniciaMerchantClientRepository
) : FindFeeniciaMerchantInPort {

    override fun execute(merchantId: UUID): FeeniciaMerchant =
        log.benchmark("Find Feenicia Merchant with id: $merchantId") {
            repository.findBy(merchantId)
                .log { info("Feenicia Merchant found: {}", it) }
        }
    companion object : CompanionLogger()
}
