package com.kiwi.api.reimbursements.hexagonal.adapter.out.db

import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.mapper.ToOperationMapper
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedRefund
import com.kiwi.api.reimbursements.hexagonal.application.aOperation
import com.kiwi.api.reimbursements.hexagonal.domain.OperationType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OperationRepositorySpec : FeatureSpec({

    val dbRepository = mockk<OperationDbRepository>()
    val toOperationEntityMapper = mockk<ToOperationMapper>()

    val repository = OperationRepository(
        repository = dbRepository,
        operationMapper = toOperationEntityMapper
    )

    beforeEach { clearAllMocks() }

    feature("create refund operation") {

        scenario("successful create") {
            val aCreatedRefund = aCreatedRefund()
            val operationEntity = aOperation(OperationType.REFUND)

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
            val operationEntity = aOperation(OperationType.ANNULMENT)

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
