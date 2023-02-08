package com.menta.api.transactions.application.port.out

import com.menta.api.transactions.adapter.out.db.entity.Operation
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import jdk.jshell.Snippet.Status
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

interface OperationRepositoryOutPort {

    fun create(transaction: Transaction)
    fun find(operationId: UUID, operationType: OperationType): Optional<Operation>
    fun find(acquirerId: String, operationType: OperationType): Optional<Operation>
    fun find(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime?,
        end: OffsetDateTime?,
        status: List<StatusCode>?,
        pageable: Pageable
    ): Page<Operation>
}
