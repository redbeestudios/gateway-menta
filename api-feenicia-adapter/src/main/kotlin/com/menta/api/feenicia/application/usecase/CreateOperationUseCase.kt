package com.menta.api.feenicia.application.usecase

import arrow.core.Either
import com.menta.api.feenicia.application.port.`in`.CreateOperationInPort
import com.menta.api.feenicia.application.port.out.FeeniciaClientRepository
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.shared.error.model.ApplicationError
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val feeniciaClientRepository: FeeniciaClientRepository
) : CreateOperationInPort {

    override fun execute(operation: Operation, reverseOperationType: OperationType?): Either<ApplicationError, CreatedOperation> =
        with(operation) {
            authorize(reverseOperationType)
        }

    private fun Operation.authorize(reverseOperationType: OperationType?): Either<ApplicationError, CreatedOperation> =
        feeniciaClientRepository.execute(this, reverseOperationType)

    companion object : CompanionLogger()
}
