package com.menta.api.transactions.adapter.out.db

import com.menta.api.transactions.adapter.out.db.mapper.ToTransactionEntityMapper
import com.menta.api.transactions.application.port.out.TransactionRepositoryOutPort
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import com.menta.api.transactions.adapter.out.db.entity.Transaction as TransactionEntity

@Component
class TransactionRepository(
    private val repository: TransactionDbRepository,
    private val mapper: ToTransactionEntityMapper
) : TransactionRepositoryOutPort {

    override fun create(transaction: Transaction): TransactionEntity =
        transaction
            .toEntity()
            .save()

    private fun Transaction.toEntity() =
        mapper.map(this)
            .log { info("Transaction mapped to entity model") }

    private fun TransactionEntity.save() =
        repository.save(this)
            .log { info("Transaction entity persisted: {}", it.transactionId) }

    companion object : CompanionLogger()
}
