package com.kiwi.api.reversal.hexagonal.adapter.out.db

import com.kiwi.api.reversal.hexagonal.adapter.out.db.mapper.ToOperationEntityMapper
import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.aOperationEntity
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OperationRepositorySpec : FeatureSpec({

    val dbRepository = mockk<OperationDbRepository>()
    val toOperationEntityMapper = mockk<ToOperationEntityMapper>()

    val repository = OperationRepository(
        repository = dbRepository,
        operationEntityMapper = toOperationEntityMapper
    )

    beforeEach { clearAllMocks() }

    feature("create payment operation") {

        scenario("successful create") {
            val aCreatedPayment = aCreatedPayment()
            val operationEntity = aOperationEntity(OperationType.PAYMENT)

            // given mocked dependencies
            every { toOperationEntityMapper.map(aCreatedPayment) } returns operationEntity
            every { dbRepository.save(operationEntity) } returns operationEntity

            // expect that
            repository.create(aCreatedPayment) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { toOperationEntityMapper.map(aCreatedPayment) }
            verify(exactly = 1) { dbRepository.save(operationEntity) }
        }
    }

    feature("create refund operation") {

        scenario("successful create") {
            val aCreatedRefund = aCreatedRefund()
            val operationEntity = aOperationEntity(OperationType.REFUND)

            // given mocked dependencies
            every { toOperationEntityMapper.map(aCreatedRefund) } returns operationEntity
            every { dbRepository.save(operationEntity) } returns operationEntity

            // expect that
            repository.create(aCreatedRefund) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { toOperationEntityMapper.map(aCreatedRefund) }
            verify(exactly = 1) { dbRepository.save(operationEntity) }
        }
    }

    feature("create annulment operation") {

        scenario("successful create") {
            val aCreatedAnnulment = aCreatedAnnulment()
            val operationEntity = aOperationEntity(OperationType.ANNULMENT)

            // given mocked dependencies
            every { toOperationEntityMapper.map(aCreatedAnnulment) } returns operationEntity
            every { dbRepository.save(operationEntity) } returns operationEntity

            // expect that
            repository.create(aCreatedAnnulment) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { toOperationEntityMapper.map(aCreatedAnnulment) }
            verify(exactly = 1) { dbRepository.save(operationEntity) }
        }
    }
})
