package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface PaymentProducerPortOut {
    fun produce(payment: Payment): Either<ApplicationError, Unit>
}
