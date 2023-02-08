package com.kiwi.api.reversal.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreatePaymentPortIn {
    fun execute(payment: Payment): Either<ApplicationError, CreatedPayment>
}
