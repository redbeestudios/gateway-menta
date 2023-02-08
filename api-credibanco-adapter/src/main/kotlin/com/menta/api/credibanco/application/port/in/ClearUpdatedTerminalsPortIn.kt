package com.menta.api.credibanco.application.port.`in`

import arrow.core.Either
import com.menta.api.credibanco.shared.error.model.ApplicationError

interface ClearUpdatedTerminalsPortIn {
    fun execute(acquirer: String): Either<ApplicationError, Boolean>
}
