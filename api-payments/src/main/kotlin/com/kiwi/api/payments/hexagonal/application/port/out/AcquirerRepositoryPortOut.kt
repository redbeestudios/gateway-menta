package com.kiwi.api.payments.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface AcquirerRepositoryPortOut {
    fun authorize(payment: Payment): Either<ApplicationError, Authorization>
}
