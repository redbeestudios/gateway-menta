package com.menta.api.feenicia.application.port.out

import arrow.core.Either
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.shared.error.model.ApplicationError

interface FeeniciaClientRepository {
    fun execute(operation: Operation, reverseOperationType: OperationType? = null): Either<ApplicationError, CreatedOperation>
}
