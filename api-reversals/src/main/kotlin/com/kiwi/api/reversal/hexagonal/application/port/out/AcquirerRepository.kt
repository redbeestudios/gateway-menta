package com.kiwi.api.reversal.hexagonal.application.port.out

import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund

interface AcquirerRepository {
    fun authorize(payment: Payment): Authorization
    fun authorize(refund: Refund): Authorization
    fun authorize(annulment: Annulment): Authorization
}
