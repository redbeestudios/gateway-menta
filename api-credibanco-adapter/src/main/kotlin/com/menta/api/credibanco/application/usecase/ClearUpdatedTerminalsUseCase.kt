package com.menta.api.credibanco.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.credibanco.application.port.`in`.ClearUpdatedTerminalsPortIn
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.NoTerminalsRegistered
import org.springframework.stereotype.Component

@Component
class ClearUpdatedTerminalsUseCase(
    val terminalsRepository: TerminalsRepository
) : ClearUpdatedTerminalsPortIn {

    override fun execute(acquirer: String): Either<ApplicationError, Boolean> =
        when (terminalsRepository.deleteAll()) {
            true -> true.right()
            false -> NoTerminalsRegistered(acquirer).left()
        }
}
