package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreatedRefundProducerPortOut {
    fun produce(refund: CreatedRefund): Either<ApplicationError, Unit>
}
