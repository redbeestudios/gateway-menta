package com.menta.api.transactions.application

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.TestConstants.Companion.OPERATION_ID
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.usecase.FindTransactionOperationUseCase
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class FindTransactionOperationUseCaseSpec : FeatureSpec({

    val repository = mockk<OperationRepositoryOutPort>()
    val mapper = mockk<TransactionMapper>()

    val useCase = FindTransactionOperationUseCase(mapper, repository)

    beforeEach { clearAllMocks() }

    feature("find transaction by operation") {

        scenario("transaction found") {
            val operationId = UUID.fromString(OPERATION_ID)
            val operationType = OperationType.PAYMENT

            val operation = aOperationEntity()
            val transaction = aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID))

            every { repository.find(operationId, operationType) } returns Optional.of(operation)
            every { mapper.map(operation) } returns transaction

            useCase.execute(operationId, operationType) shouldBeRight transaction

            verify(exactly = 1) { repository.find(operationId, operationType) }
        }

        scenario("transaction NOT found") {
            val operationId = UUID.fromString(OPERATION_ID)
            val operationType = OperationType.PAYMENT

            val error = operationNotFound(operationId, operationType)

            every { repository.find(operationId, operationType) } returns Optional.empty()

            useCase.execute(operationId, operationType) shouldBeLeft error

            verify(exactly = 1) { repository.find(operationId, operationType) }
        }
    }
})
