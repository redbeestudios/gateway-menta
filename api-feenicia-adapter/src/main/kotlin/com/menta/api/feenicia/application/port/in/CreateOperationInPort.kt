package com.menta.api.feenicia.application.port.`in`

import arrow.core.Either
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.shared.error.model.ApplicationError

interface CreateOperationInPort {
    fun execute(operation: Operation, reverseOperationType: OperationType? = null): Either<ApplicationError, CreatedOperation>
}
