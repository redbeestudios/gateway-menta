package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError

interface CreateAnnulmentPortIn {
    fun execute(annulment: Annulment): Either<ApplicationError, CreatedAnnulment>
}
