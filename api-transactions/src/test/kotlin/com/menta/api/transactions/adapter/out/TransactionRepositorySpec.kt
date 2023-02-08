package com.menta.api.transactions.adapter.out

import com.menta.api.transactions.TestConstants.Companion.OPERATION_ID
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.adapter.out.db.OperationDbRepository
import com.menta.api.transactions.adapter.out.db.OperationRepository
import com.menta.api.transactions.adapter.out.db.mapper.ToOperationEntityMapper
import com.menta.api.transactions.adapter.out.db.mapper.ToTransactionEntityMapper
import com.menta.api.transactions.domain.OperationType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class TransactionRepositorySpec : FeatureSpec({

    val dbRepository = mockk<OperationDbRepository>()
    val repository = OperationRepository(
        repository = dbRepository,
        operationEntityMapper = ToOperationEntityMapper(
            transactionMapper = ToTransactionEntityMapper()
        )
    )

    beforeEach { clearAllMocks() }

    feature("find by operation id and operation type") {

        scenario("transaction found") {
            val operationId = UUID.fromString(OPERATION_ID)
            val operationType = OperationType.PAYMENT

            val operation = aOperationEntity()

            every {
                dbRepository.findByIdAndType(operationId, operationType.name)
            } returns Optional.of(operation)

            repository.find(operationId, operationType) shouldBe Optional.of(operation)

            verify(exactly = 1) { dbRepository.findByIdAndType(operationId, operationType.name) }
        }

        scenario("transaction NOT found") {
            val operationId = UUID.fromString(OPERATION_ID)
            val operationType = OperationType.PAYMENT

            every {
                dbRepository.findByIdAndType(operationId, operationType.name)
            } returns Optional.empty()

            repository.find(operationId, operationType) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByIdAndType(operationId, operationType.name) }
        }
    }
})
