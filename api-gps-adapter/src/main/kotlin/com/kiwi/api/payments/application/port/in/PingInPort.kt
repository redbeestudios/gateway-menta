package com.kiwi.api.payments.application.port.`in`

import arrow.core.Either
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface PingInPort {
    fun execute(): Either<ApplicationError, Unit>
}
