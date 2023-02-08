package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreatedAnnulmentProducerPortOut {
    fun produce(annulment: CreatedAnnulment): Either<ApplicationError, Unit>
}
