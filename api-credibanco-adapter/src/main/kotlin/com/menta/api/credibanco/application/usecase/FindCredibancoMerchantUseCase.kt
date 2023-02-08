package com.menta.api.credibanco.application.usecase

import com.menta.api.credibanco.application.port.`in`.FindCredibancoMerchantPortIn
import com.menta.api.credibanco.application.port.out.CredibancoMerchantRepositoryPortOut
import com.menta.api.credibanco.domain.CredibancoMerchant
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import com.menta.api.credibanco.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCredibancoMerchantUseCase(
    private val repository: CredibancoMerchantRepositoryPortOut
) : FindCredibancoMerchantPortIn {

    override fun execute(merchantId: UUID): CredibancoMerchant =
        log.benchmark("Find Credibanco Merchant with id: $merchantId") {
            repository.findBy(merchantId)
                .log { info("Credibanco Merchant found: {}", it) }
        }
    companion object : CompanionLogger()
}
