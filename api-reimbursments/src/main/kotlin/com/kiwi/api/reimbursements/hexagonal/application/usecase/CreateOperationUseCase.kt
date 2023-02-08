package com.kiwi.api.reimbursements.hexagonal.application.usecase

import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.reimbursements.hexagonal.application.port.out.OperationRepositoryOutPort
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val operationRepository: OperationRepositoryOutPort
) : CreateOperationInPort {

    override fun execute(createdRefund: CreatedRefund) =
        createdRefund
            .create()

    override fun execute(createdAnnulment: CreatedAnnulment) =
        createdAnnulment
            .create()

    private fun CreatedRefund.create() =
        operationRepository.create(this)
            .log { info("refund created: {}", it) }
    private fun CreatedAnnulment.create() =
        operationRepository.create(this)
            .log { info("annulment created: {}", it) }

    companion object : CompanionLogger()
}
