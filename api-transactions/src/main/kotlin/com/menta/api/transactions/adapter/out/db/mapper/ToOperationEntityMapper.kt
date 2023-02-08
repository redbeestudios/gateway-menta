package com.menta.api.transactions.adapter.out.db.mapper

import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import com.menta.api.transactions.adapter.out.db.entity.Operation as OperationEntity

@Component
class ToOperationEntityMapper(
    private val transactionMapper: ToTransactionEntityMapper
) {
    fun map(transaction: Transaction) =
        OperationEntity(
            id = transaction.operation.id,
            ticketId = transaction.operation.ticketId,
            type = transaction.operation.type.name,
            status = transaction.operation.status,
            datetime = transaction.operation.datetime,
            transaction = transactionMapper.map(transaction),
            acquirerId = transaction.operation.acquirerId
        ).log { info("Operation entity mapped: {}", it) }

    companion object : CompanionLogger()
}
