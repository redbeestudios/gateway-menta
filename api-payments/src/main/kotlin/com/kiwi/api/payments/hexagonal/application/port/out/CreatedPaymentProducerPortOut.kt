package com.kiwi.api.payments.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface CreatedPaymentProducerPortOut {
    fun produce(message: CreatedPayment): Either<ApplicationError, Unit>
}
