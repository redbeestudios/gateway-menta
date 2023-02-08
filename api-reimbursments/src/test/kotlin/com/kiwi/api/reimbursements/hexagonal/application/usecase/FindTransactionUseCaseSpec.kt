package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.left
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.aTransaction
import com.kiwi.api.reimbursements.hexagonal.application.acquirerId
import com.kiwi.api.reimbursements.hexagonal.application.paymentId
import com.kiwi.api.reimbursements.hexagonal.application.port.out.TransactionRepositoryPortOut
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindTransactionUseCaseSpec : FeatureSpec({

    feature("transaction search") {

        lateinit var transactionRepository: TransactionRepositoryPortOut

        lateinit var useCase: FindTransactionUseCase

        beforeEach {
            transactionRepository = mockk()

            useCase = FindTransactionUseCase(
                transactionRepository = transactionRepository
            )
        }

        scenario("successful transaction search") {
            val transaction = aTransaction()
            val transactionId = paymentId

            // given mocked dependencies
            every { transactionRepository.retrieve(transactionId) } returns transaction.right()

            // expect that
            useCase.execute(transactionId) shouldBeRight transaction

            // dependencies called
            verify(exactly = 1) { transactionRepository.retrieve(transactionId) }
        }

        scenario("transaction NOT FOUND") {
            val transactionId = paymentId

            // given mocked dependencies
            every { transactionRepository.retrieve(transactionId) } returns
                ApplicationError.transactionNotFound(acquirerId).left()

            // expect that
            useCase.execute(transactionId) shouldBeLeft ApplicationError.transactionNotFound(acquirerId)

            // dependencies called
            verify(exactly = 1) { transactionRepository.retrieve(transactionId) }
        }
    }
})
