package com.menta.api.transactions.application.usecase

import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.port.out.TransactionRepositoryOutPort
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateTransactionUseCase(
    private val transactionRepository: TransactionRepositoryOutPort,
    private val operationRepository: OperationRepositoryOutPort
) : CreateTransactionInPort {
    override fun execute(transaction: Transaction) =
        transaction
            .also {
                transactionRepository.create(it)
                    .log { info("Transaction created: {}", it) }
            }
            .also {
                operationRepository.create(it)
                    .log { info("Operation created: {}", it) }
            }.id

    companion object : CompanionLogger()
}
