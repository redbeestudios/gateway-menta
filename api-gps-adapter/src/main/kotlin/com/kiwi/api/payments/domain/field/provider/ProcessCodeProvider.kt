package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.domain.field.ProcessCode.AccountType.DEFAULT
import com.kiwi.api.payments.domain.field.mapper.TransactionTypeMapper
import org.springframework.stereotype.Component

@Component
class ProcessCodeProvider(
    private val transactionTypeMapper: TransactionTypeMapper
) {

    fun provide(operationType: OperationType) =
        ProcessCode(
            transactionType = transactionTypeMapper.map(operationType),
            accountType = DEFAULT,
            followMessage = false
        )
}
