package com.kiwi.api.reversal.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError

interface CreateAnnulmentPortIn {
    fun execute(annulment: Annulment): Either<ApplicationError, CreatedAnnulment>
}
