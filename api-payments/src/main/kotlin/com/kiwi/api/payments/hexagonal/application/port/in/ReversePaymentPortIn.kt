package com.kiwi.api.payments.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface ReversePaymentPortIn {
    fun execute(reversalOperation: ReversalOperation): Either<ApplicationError, ReversalOperation>
}
