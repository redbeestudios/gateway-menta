package com.kiwi.api.payments.application.usecase

import arrow.core.Either
import com.kiwi.api.payments.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.application.port.out.GlobalClientRepository
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val globalClientRepository: GlobalClientRepository
) : CreateOperationInPort {

    override fun execute(operation: Operation): Either<ApplicationError, CreatedOperation> =
        operation.authorize()

    private fun Operation.authorize(): Either<ApplicationError, CreatedOperation> =
        globalClientRepository.authorize(this)
            .logRight { info("operation created") }

    companion object : CompanionLogger()
}
