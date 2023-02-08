package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTransactionPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.TransactionRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindTransactionUseCase(
    private val transactionRepository: TransactionRepositoryPortOut
) : FindTransactionPortIn {

    override fun execute(paymentId: UUID): Either<ApplicationError, Transaction> =
        transactionRepository.retrieve(paymentId)
}
