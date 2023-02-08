package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.port.`in`.ReversePaymentPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.application.port.out.ReverseOperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReversePaymentUseCase(
    private val reversalPaymentRepository: ReverseOperationRepositoryPortOut,
    private val operationRepositoryPortOut: OperationRepositoryPortOut
) : ReversePaymentPortIn {

    override fun execute(reversalOperation: ReversalOperation): Either<ApplicationError, ReversalOperation> =
        reversalOperation.hastId()
            .flatMap { reversalPaymentRepository.produce(it) }
            .logRight { info("Payment reversal: {}", reversalOperation) }

    companion object : CompanionLogger()

    fun ReversalOperation.hastId(): Either<ApplicationError, ReversalOperation> =
        if (operationId == null) {
            getId(this)
                .map { this.copy(operationId = it) }
                .logRight { info("Found Operation id for Reversal") }
        } else {
            this.right().log { info("Payment to reverse with id: {}", operationId) }
        }

    fun getId(reversalOperation: ReversalOperation): Either<ApplicationError, UUID> =
        operationRepositoryPortOut.getId(reversalOperation)
}
