package com.kiwi.api.reversal.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface AnnulmentProducerPortOut {
    fun produce(annulment: Annulment): Either<ApplicationError, Unit>
}
