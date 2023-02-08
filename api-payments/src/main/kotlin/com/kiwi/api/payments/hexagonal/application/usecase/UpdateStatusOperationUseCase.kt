package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.application.port.`in`.UpdateStatusOperationPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import org.springframework.stereotype.Component

@Component
class UpdateStatusOperationUseCase(private val operationRepository: OperationRepositoryPortOut) : UpdateStatusOperationPortIn {
    override fun execute(reversalOperation: ReversalOperation): Either<ApplicationError, Unit> =
        operationRepository.updateStatusForReversal(reversalOperation)
}
