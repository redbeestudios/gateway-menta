package com.kiwi.api.payments.domain.field.mapper

import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.OperationType.ANNULMENT
import com.kiwi.api.payments.domain.OperationType.ANNULMENT_REVERSE
import com.kiwi.api.payments.domain.OperationType.PURCHASE
import com.kiwi.api.payments.domain.OperationType.PURCHASE_REVERSE
import com.kiwi.api.payments.domain.OperationType.REFUND
import com.kiwi.api.payments.domain.OperationType.REFUND_REVERSE
import com.kiwi.api.payments.domain.field.ProcessCode.TransactionType
import org.springframework.stereotype.Component

@Component
class TransactionTypeMapper {
    fun map(operationType: OperationType): TransactionType =
        when (operationType) {
            PURCHASE, PURCHASE_REVERSE -> TransactionType.PURCHASE
            ANNULMENT, ANNULMENT_REVERSE -> TransactionType.PURCHASE_ANNULMENT
            REFUND, REFUND_REVERSE -> TransactionType.REFUND
        }
}
