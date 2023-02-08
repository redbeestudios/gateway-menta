package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.left
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedRefund
import com.kiwi.api.reimbursements.hexagonal.application.aRefund
import com.kiwi.api.reimbursements.hexagonal.application.anAuthorization
import com.kiwi.api.reimbursements.hexagonal.application.id
import com.kiwi.api.reimbursements.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedRefundRepositoryPorOut
import com.kiwi.api.reimbursements.hexagonal.domain.mapper.ToCreatedRefundMapper
import com.kiwi.api.reimbursements.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.queueProducerNotWritten
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateRefundUseCaseSpec : FeatureSpec({

    feature("refund creation") {

        val acquireRepository = mockk<AcquirerRepository>()
        val createdAnnulmentsRepositoryPortOut = mockk<CreatedRefundRepositoryPorOut>()
        val idProvider = mockk<IdProvider>()
        val mapper = mockk<ToCreatedRefundMapper>()

        val useCase = CreateRefundUseCase(
            acquirerRepository = acquireRepository,
            createdRefundRepository = createdAnnulmentsRepositoryPortOut,
            idProvider = idProvider,
            toCreatedRefundMapper = mapper
        )

        beforeEach { clearAllMocks() }

        scenario("successful refund creation") {

            val expectedReimbursement = aRefund()
            val authorization = anAuthorization()
            val createdRefund = aCreatedRefund()

            // given mocked dependencies
            every { acquireRepository.authorizeRefund(expectedReimbursement) } returns authorization
            every { idProvider.provide() } returns id
            every { mapper.from(expectedReimbursement, authorization, id) } returns createdRefund
            every { createdAnnulmentsRepositoryPortOut.save(createdRefund) } returns Unit.right()

            // expect that
            useCase.execute(expectedReimbursement) shouldBeRight createdRefund

            // dependencies called
            verify(exactly = 1) { acquireRepository.authorizeRefund(expectedReimbursement) }
            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { mapper.from(expectedReimbursement, authorization, id) }
            verify(exactly = 1) { createdAnnulmentsRepositoryPortOut.save(createdRefund) }
        }

        scenario("failed producing message") {

            val expectedReimbursement = aRefund()
            val authorization = anAuthorization()
            val createdRefund = aCreatedRefund()

            // given mocked dependencies
            every { acquireRepository.authorizeRefund(expectedReimbursement) } returns authorization
            every { idProvider.provide() } returns id
            every { mapper.from(expectedReimbursement, authorization, id) } returns createdRefund
            every { createdAnnulmentsRepositoryPortOut.save(createdRefund) } returns queueProducerNotWritten().left()

            // expect that
            useCase.execute(expectedReimbursement) shouldBeLeft queueProducerNotWritten()

            // dependencies called
            verify(exactly = 1) { acquireRepository.authorizeRefund(expectedReimbursement) }
            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { mapper.from(expectedReimbursement, authorization, id) }
            verify(exactly = 1) { createdAnnulmentsRepositoryPortOut.save(createdRefund) }
        }
    }
})
