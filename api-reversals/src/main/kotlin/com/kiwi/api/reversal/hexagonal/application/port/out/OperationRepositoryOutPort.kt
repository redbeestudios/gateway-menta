package com.kiwi.api.reversal.hexagonal.application.port.out

import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund

interface OperationRepositoryOutPort {
    fun create(createdPayment: CreatedPayment)
    fun create(createdRefund: CreatedRefund)
    fun create(createdAnnulment: CreatedAnnulment)
}
