package com.kiwi.api.reimbursements.hexagonal.application.port.out

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError

interface CreatedRefundRepositoryPorOut {
    fun save(createdRefund: CreatedRefund): Either<ApplicationError, Unit>
}
