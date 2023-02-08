package com.kiwi.api.payments.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface ReverseOperationRepositoryPortOut {
    fun produce(reversalOperation: ReversalOperation): Either<ApplicationError, ReversalOperation>
}
