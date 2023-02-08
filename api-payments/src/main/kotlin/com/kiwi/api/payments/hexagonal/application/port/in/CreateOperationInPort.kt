package com.kiwi.api.payments.hexagonal.application.port.`in`

import com.kiwi.api.payments.hexagonal.domain.CreatedPayment

interface CreateOperationInPort {
    fun execute(createdPayment: CreatedPayment)
}
