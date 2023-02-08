package com.menta.api.transactions.application

import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_RRN
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.usecase.FindTransactionAcquirerUseCase
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

class FindTransactionAcquirerUseCaseSpec : FeatureSpec({

    val repository = mockk<OperationRepositoryOutPort>()
    val mapper = mockk<TransactionMapper>()

    val useCase = FindTransactionAcquirerUseCase(mapper, repository)

    beforeEach { clearAllMocks() }

    feature("find transaction by acquirer") {

        scenario("transaction found") {
            val acquirerId = AUTHORIZATION_RRN
            val operationType = OperationType.PAYMENT

            val operation = aOperationEntity()
            val transaction = aTransaction(UUID.fromString(TRANSACTION_ID))

            every { repository.find(acquirerId, operationType) } returns Optional.of(operation)
            every { mapper.map(operation) } returns transaction

            useCase.execute(acquirerId, operationType) shouldBeRight transaction

            verify(exactly = 1) { repository.find(acquirerId, operationType) }
        }

        scenario("transaction NOT found") {
            val acquirerId = AUTHORIZATION_RRN
            val operationType = OperationType.PAYMENT

            val error = operationNotFound(acquirerId, operationType)

            every { repository.find(acquirerId, operationType) } returns Optional.empty()

            useCase.execute(acquirerId, operationType) shouldBeLeft error

            verify(exactly = 1) { repository.find(acquirerId, operationType) }
        }
    }
})
