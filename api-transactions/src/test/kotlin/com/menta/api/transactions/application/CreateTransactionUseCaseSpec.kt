package com.menta.api.transactions.application

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.aTransactionEntity
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.port.out.TransactionRepositoryOutPort
import com.menta.api.transactions.application.usecase.CreateTransactionUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateTransactionUseCaseSpec : FeatureSpec({
    feature("create operation") {
        lateinit var operationRepository: OperationRepositoryOutPort
        lateinit var transactionRepository: TransactionRepositoryOutPort
        lateinit var useCase: CreateTransactionUseCase

        beforeEach {
            operationRepository = mockk()
            transactionRepository = mockk()
            useCase = CreateTransactionUseCase(
                operationRepository = operationRepository,
                transactionRepository = transactionRepository
            )
        }
        scenario("successful create") {
            val transaction = aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID))
            val transactionEntity = aTransactionEntity()

            // given mocked dependencies
            every { operationRepository.create(transaction) } returns Unit
            every { transactionRepository.create(transaction) } returns transactionEntity

            // expect that
            useCase.execute(transaction)

            // dependencies called
            verify(exactly = 1) { operationRepository.create(transaction) }
            verify(exactly = 1) { transactionRepository.create(transaction) }
        }
    }
})
