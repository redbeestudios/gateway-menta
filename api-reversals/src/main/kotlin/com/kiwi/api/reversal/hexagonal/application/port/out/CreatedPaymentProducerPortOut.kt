package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreatedPaymentProducerPortOut {
    fun produce(payment: CreatedPayment): Either<ApplicationError, Unit>
}
