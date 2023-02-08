package com.kiwi.api.reversal.hexagonal.application.usecase.entities

import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.MerchantRepositoryPortOut
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindMerchantUseCase(
    private val merchantRepository: MerchantRepositoryPortOut,
) : FindMerchantPortIn {

    override fun execute(merchantId: UUID): Merchant =
        merchantRepository.retrieve(merchantId)
}
