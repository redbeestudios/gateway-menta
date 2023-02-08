package com.menta.api.credibanco.application.usecase

import arrow.core.Either
import com.menta.api.credibanco.application.port.`in`.CreateOperationPortIn
import com.menta.api.credibanco.application.port.out.CredibancoRepository
import com.menta.api.credibanco.application.port.out.OperationRepositoryPortOut
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val credibancoRepository: CredibancoRepository,
    private val operationRepository: OperationRepositoryPortOut
) : CreateOperationPortIn {

    override fun execute(operation: Operation): Either<ApplicationError, CreatedOperation> =
        operation
            .authorize()
            .also { it.map { createdOperation -> createdOperation.save() } }

    private fun Operation.authorize(): Either<ApplicationError, CreatedOperation> =
        credibancoRepository.authorize(this)
            .logRight { info("operation created") }

    private fun CreatedOperation.save() = operationRepository.save(this)
        .log { info("response operation saved: {}", it) }

    companion object : CompanionLogger()
}
