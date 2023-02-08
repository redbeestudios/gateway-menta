package com.kiwi.api.reversal.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreateRefundPortIn {
    fun execute(refund: Refund): Either<ApplicationError, CreatedRefund>
}
