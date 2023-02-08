package com.menta.api.banorte.application.usecase

import arrow.core.Either
import com.menta.api.banorte.application.port.`in`.CreateOperationInPort
import com.menta.api.banorte.application.port.out.BanorteClientRepository
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val banorteClientRepository: BanorteClientRepository
) : CreateOperationInPort {

    override fun execute(operation: Operation): Either<ApplicationError, CreatedOperation> =
        with(operation) {
            authorize()
        }

    private fun Operation.authorize(): Either<ApplicationError, CreatedOperation> =
        banorteClientRepository.authorize(this)

    companion object : CompanionLogger()
}
