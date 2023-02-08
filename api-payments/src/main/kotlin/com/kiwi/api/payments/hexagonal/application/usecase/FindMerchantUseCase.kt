package com.kiwi.api.payments.hexagonal.application.usecase

import com.kiwi.api.payments.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.MerchantRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.Payment
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindMerchantUseCase(
    private val merchantRepository: MerchantRepositoryPortOut,
) : FindMerchantPortIn {

    override fun execute(merchantId: UUID): Payment.Merchant =
        merchantRepository.retrieve(merchantId)
}
