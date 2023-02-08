package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError

interface CreateRefundPortIn {
    fun execute(refund: Refund): Either<ApplicationError, CreatedRefund>
}
