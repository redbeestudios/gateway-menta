package com.kiwi.api.reversal.hexagonal.application.port.`in`

import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund

interface CreateOperationInPort {
    fun execute(createdPayment: CreatedPayment)
    fun execute(createdRefund: CreatedRefund)
    fun execute(createdAnnulment: CreatedAnnulment)
}
