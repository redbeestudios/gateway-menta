package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.reversal.hexagonal.application.port.out.OperationRepositoryOutPort
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val operationRepository: OperationRepositoryOutPort
) : CreateOperationInPort {

    override fun execute(createdPayment: CreatedPayment) =
        createdPayment
            .create()

    override fun execute(createdRefund: CreatedRefund) =
        createdRefund
            .create()

    override fun execute(createdAnnulment: CreatedAnnulment) =
        createdAnnulment
            .create()

    private fun CreatedPayment.create() = operationRepository.create(this)

    private fun CreatedRefund.create() = operationRepository.create(this)

    private fun CreatedAnnulment.create() = operationRepository.create(this)
}
