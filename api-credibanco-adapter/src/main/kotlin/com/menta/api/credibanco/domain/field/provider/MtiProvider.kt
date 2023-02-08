package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.OperationType.ANNULMENT
import com.menta.api.credibanco.domain.OperationType.ANNULMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PAYMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.domain.OperationType.REFUND
import com.menta.api.credibanco.domain.field.MTI
import com.menta.api.credibanco.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.menta.api.credibanco.domain.field.MTI.REVERSE_REQUEST
import org.springframework.stereotype.Component

@Component
class MtiProvider {
    fun provide(transactionType: OperationType): MTI {
        return when (transactionType) {
            PURCHASE, ANNULMENT, REFUND -> ONLINE_OPERATION_REQUEST
            PAYMENT_REVERSE, ANNULMENT_REVERSE -> REVERSE_REQUEST
        }
    }
}
