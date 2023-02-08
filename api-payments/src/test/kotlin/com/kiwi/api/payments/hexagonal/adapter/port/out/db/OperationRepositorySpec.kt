package com.kiwi.api.payments.hexagonal.adapter.port.out.db

import arrow.core.right
import com.kiwi.api.payments.hexagonal.adapter.out.db.OperationDbRepository
import com.kiwi.api.payments.hexagonal.adapter.out.db.OperationRepository
import com.kiwi.api.payments.hexagonal.adapter.out.db.mapper.ToOperationEntityMapper
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.aOperationEntity
import com.kiwi.api.payments.hexagonal.application.aOperationEntityWithStatusReversal
import com.kiwi.api.payments.hexagonal.application.aOperationId
import com.kiwi.api.payments.hexagonal.application.aReversalOperaionWithoutOperationId
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class OperationRepositorySpec : FeatureSpec({

    val dbRepository = mockk<OperationDbRepository>()
    val toOperationEntityMapper = mockk<ToOperationEntityMapper>()

    val repository = OperationRepository(
        repository = dbRepository,
        operationEntityMapper = toOperationEntityMapper
    )

    beforeEach { clearAllMocks() }

    feature("create operation") {

        scenario("successful create") {
            val createdPaymentMessage = aCreatedPayment()
            val operationEntity = aOperationEntity()

            // given mocked dependencies
            every { toOperationEntityMapper.map(createdPaymentMessage) } returns operationEntity
            every { dbRepository.save(operationEntity) } returns operationEntity

            // expect that
            repository.create(createdPaymentMessage) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { toOperationEntityMapper.map(createdPaymentMessage) }
            verify(exactly = 1) { dbRepository.save(operationEntity) }
        }

        scenario("get id for operation") {
            val reversalOperation = aReversalOperaionWithoutOperationId()
            val operationEntity = aOperationEntity()

            // given mocked dependencies
            every {
                dbRepository.findByTraceAndTicketAndBatchAndTerminalIdAndAmountAndDatetime(
                    reversalOperation.trace,
                    reversalOperation.ticket,
                    reversalOperation.batch,
                    reversalOperation.terminal.id,
                    reversalOperation.amount.total,
                    reversalOperation.datetime
                )
            } returns Optional.of(operationEntity)

            // expect that
            repository.getId(reversalOperation) shouldBe operationEntity.id.right()

            // dependencies called
            verify(exactly = 1) {
                dbRepository.findByTraceAndTicketAndBatchAndTerminalIdAndAmountAndDatetime(
                    reversalOperation.trace,
                    reversalOperation.ticket,
                    reversalOperation.batch,
                    reversalOperation.terminal.id,
                    reversalOperation.amount.total,
                    reversalOperation.datetime
                )
            }
        }

        scenario("Update status for payment") {
            val reversalOperation = aReversalOperation()
            val operationEntity = aOperationEntity()
            val operationEntityReversal = aOperationEntityWithStatusReversal()

            // given mocked dependencies
            every { dbRepository.findById(aOperationId) } returns Optional.of(operationEntity)
            every { toOperationEntityMapper.map(operationEntity) } returns operationEntityReversal
            every { dbRepository.save(operationEntityReversal) } returns operationEntityReversal

            // expect that
            repository.updateStatusForReversal(reversalOperation) shouldBe Unit.right()

            // dependencies called
            verify(exactly = 1) { dbRepository.findById(aOperationId) }
            verify(exactly = 1) { toOperationEntityMapper.map(operationEntity) }
            verify(exactly = 1) { dbRepository.save(operationEntityReversal) }
        }
    }
})
