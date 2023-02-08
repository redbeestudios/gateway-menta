package com.menta.api.transactions.application.usecase

import arrow.core.Either
import com.menta.api.transactions.adapter.out.db.entity.Operation
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.`in`.FindTransactionAcquirerInPort
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

@Component
@Transactional
class FindTransactionAcquirerUseCase(
    private val transactionMapper: TransactionMapper,
    private val operationRepository: OperationRepositoryOutPort
) : FindTransactionAcquirerInPort {
    override fun execute(acquirerId: String, operationType: OperationType): Either<ApplicationError, Transaction> =
        findBy(acquirerId, operationType)
            .shouldBePresent(acquirerId, operationType).map { transactionMapper.map(it) }

    private fun findBy(acquirerId: String, operationType: OperationType) =
        operationRepository.find(acquirerId, operationType)
            .log { info("operation for acquirer id {} of type {} searched", acquirerId, operationType.name) }

    private fun Optional<Operation>.shouldBePresent(acquirerId: String, operationType: OperationType) =
        rightIfPresent(error = operationNotFound(acquirerId, operationType))
            .logEither(
                { error("operation for acquirer id {} of type {} not found", acquirerId, operationType.name) },
                { info("operation for acquirer id {} of type {} found", acquirerId, operationType.name) }
            )

    companion object : CompanionLogger()
}
