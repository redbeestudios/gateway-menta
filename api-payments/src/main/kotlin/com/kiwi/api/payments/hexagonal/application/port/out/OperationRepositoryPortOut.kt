package com.kiwi.api.payments.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import java.util.UUID

interface OperationRepositoryPortOut {
    fun create(createdPayment: CreatedPayment)
    fun getId(reversalOperation: ReversalOperation): Either<ApplicationError, UUID>
    fun updateStatusForReversal(reversalOperation: ReversalOperation): Either<ApplicationError, Unit>
}
