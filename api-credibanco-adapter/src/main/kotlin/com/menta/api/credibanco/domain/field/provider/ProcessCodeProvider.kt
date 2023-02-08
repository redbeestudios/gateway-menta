package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.domain.CardType.CREDIT
import com.menta.api.credibanco.domain.CardType.DEBIT
import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.OperationType.ANNULMENT
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.domain.field.ProcessCode
import com.menta.api.credibanco.domain.field.ProcessCode.CREDIT_ANNULMENT
import com.menta.api.credibanco.domain.field.ProcessCode.CREDIT_PURCHASE
import com.menta.api.credibanco.domain.field.ProcessCode.DEBIT_ANNULMENT
import com.menta.api.credibanco.domain.field.ProcessCode.DEBIT_PURCHASE
import org.springframework.stereotype.Component

@Component
class ProcessCodeProvider {

    fun provider(cardType: String, operationType: OperationType): ProcessCode =
        when {
            PURCHASE == operationType && DEBIT.name == cardType -> DEBIT_PURCHASE
            ANNULMENT == operationType && DEBIT.name == cardType -> DEBIT_ANNULMENT
            PURCHASE == operationType && CREDIT.name == cardType -> CREDIT_PURCHASE
            ANNULMENT == operationType && CREDIT.name == cardType -> CREDIT_ANNULMENT

            else -> { DEBIT_PURCHASE }
        }
}
