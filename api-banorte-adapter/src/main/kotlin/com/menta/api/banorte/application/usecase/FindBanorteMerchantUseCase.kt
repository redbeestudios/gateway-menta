package com.menta.api.banorte.application.usecase

import arrow.core.Either
import com.menta.api.banorte.application.port.`in`.FindBanorteMerchantInPort
import com.menta.api.banorte.application.port.out.BanorteMerchantClientRepository
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.util.log.CompanionLogger
import com.menta.api.banorte.shared.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class FindBanorteMerchantUseCase(
    private val repository: BanorteMerchantClientRepository
) : FindBanorteMerchantInPort {

    override fun execute(merchantId: String): Either<ApplicationError, BanorteMerchant> =
        log.benchmark("Find Banorte Merchant with id: $merchantId") {
            repository.findBy(merchantId)
                .log { info("Banorte Merchant found: {}", it) }
        }

    companion object : CompanionLogger()
}
