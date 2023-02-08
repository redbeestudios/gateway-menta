package com.menta.api.transactions.application.port.`in`

import com.menta.api.transactions.domain.Transaction
import java.util.UUID

interface CreateTransactionInPort {
    fun execute(transaction: Transaction): UUID
}
