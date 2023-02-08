package com.kiwi.api.reimbursements.hexagonal.application.usecase

import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.MerchantRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindMerchantUseCase(
    private val merchantRepository: MerchantRepositoryPortOut,
) : FindMerchantPortIn {

    override fun execute(merchantId: UUID): Merchant =
        merchantRepository.retrieve(merchantId)
}
