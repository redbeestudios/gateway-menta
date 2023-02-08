package com.menta.api.transactions.application.usecase

import arrow.core.Either
import com.menta.api.transactions.adapter.out.db.entity.Operation
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.error.model.ApplicationError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import com.menta.api.transactions.shared.util.either.rightIfPresent
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Component
@Transactional
class FindTransactionOperationUseCase(
    private val transactionMapper: TransactionMapper,
    private val operationRepository: OperationRepositoryOutPort
) : FindTransactionOperationInPort {
    override fun execute(operationId: UUID, operationType: OperationType): Either<ApplicationError, Transaction> =
        findBy(operationId, operationType)
            .shouldBePresent(operationId, operationType).map { transactionMapper.map(it) }

    private fun findBy(operationId: UUID, operationType: OperationType) =
        operationRepository.find(operationId, operationType)
            .log { info("operation {} of type {} searched", operationId, operationType.name) }

    private fun Optional<Operation>.shouldBePresent(operationId: UUID, operationType: OperationType) =
        rightIfPresent(error = operationNotFound(operationId, operationType))
            .logEither(
                { error("operation {} of type {} not found", operationId, operationType.name) },
                { info("operation {} of type {} found: {}", operationId, operationType.name, it) }
            )

    companion object : CompanionLogger()
}
