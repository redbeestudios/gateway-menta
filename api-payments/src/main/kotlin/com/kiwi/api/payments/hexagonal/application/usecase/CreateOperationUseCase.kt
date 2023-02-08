package com.kiwi.api.payments.hexagonal.application.usecase

import com.kiwi.api.payments.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.application.usecase.CreatePaymentUseCase.Companion.log
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val operationRepository: OperationRepositoryPortOut
) : CreateOperationInPort {

    override fun execute(createdPayment: CreatedPayment) =
        createdPayment
            .create()

    private fun CreatedPayment.create() = operationRepository.create(this)
        .log { info("operation created: {}", it) }
}
