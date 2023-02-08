package com.menta.api.transactions.application.port.`in`

import arrow.core.Either
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import com.menta.api.transactions.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import java.time.OffsetDateTime
import java.util.UUID

interface FindTransactionByFilterInPort {
    fun execute(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime?,
        end: OffsetDateTime?,
        page: Int,
        size: Int,
        status: List<StatusCode>?
    ): Either<ApplicationError, Page<Transaction>>
}
