package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface RefundProducerPortOut {
    fun produce(refund: Refund): Either<ApplicationError, Unit>
}
