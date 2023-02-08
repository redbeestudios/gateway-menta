package com.menta.api.transactions.application.port.out

import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.adapter.out.db.entity.Transaction as TransactionEntity

interface TransactionRepositoryOutPort {
    fun create(transaction: Transaction): TransactionEntity
}
