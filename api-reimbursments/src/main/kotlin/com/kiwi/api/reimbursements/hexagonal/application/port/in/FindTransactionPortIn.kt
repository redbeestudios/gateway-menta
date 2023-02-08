package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import java.util.UUID

interface FindTransactionPortIn {

    fun execute(paymentId: UUID): Either<ApplicationError, Transaction>
}
