package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.OperationType.ANNULMENT
import com.kiwi.api.payments.domain.OperationType.ANNULMENT_REVERSE
import com.kiwi.api.payments.domain.OperationType.PURCHASE
import com.kiwi.api.payments.domain.OperationType.PURCHASE_REVERSE
import com.kiwi.api.payments.domain.OperationType.REFUND
import com.kiwi.api.payments.domain.OperationType.REFUND_REVERSE
import com.kiwi.api.payments.domain.field.MTI
import com.kiwi.api.payments.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.kiwi.api.payments.domain.field.MTI.REVERSE_REQUEST
import org.springframework.stereotype.Component

@Component
class MtiProvider {
    fun provide(transactionType: OperationType): MTI {
        return when (transactionType) {
            PURCHASE, ANNULMENT, REFUND -> ONLINE_OPERATION_REQUEST
            REFUND_REVERSE, PURCHASE_REVERSE, ANNULMENT_REVERSE -> REVERSE_REQUEST
        }
    }
}
