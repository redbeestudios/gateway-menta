package com.menta.api.transactions.application.port.`in`

import arrow.core.Either
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.error.model.ApplicationError

interface FindTransactionAcquirerInPort {
    fun execute(acquirerId: String, operationType: OperationType): Either<ApplicationError, Transaction>
}
