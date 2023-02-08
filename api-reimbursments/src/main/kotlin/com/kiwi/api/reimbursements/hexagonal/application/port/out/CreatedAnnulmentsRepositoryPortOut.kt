package com.kiwi.api.reimbursements.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError

interface CreatedAnnulmentsRepositoryPortOut {
    fun save(createdAnnulment: CreatedAnnulment): Either<ApplicationError, Unit>
}
