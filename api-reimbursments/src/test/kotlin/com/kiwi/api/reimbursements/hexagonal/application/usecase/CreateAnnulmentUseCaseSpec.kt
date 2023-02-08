package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.left
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.anAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.anAuthorization
import com.kiwi.api.reimbursements.hexagonal.application.id
import com.kiwi.api.reimbursements.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedAnnulmentsRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.mapper.ToCreatedAnnulmentMapper
import com.kiwi.api.reimbursements.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.queueProducerNotWritten
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateAnnulmentUseCaseSpec : FeatureSpec({

    feature("annulment creation") {

        val acquireRepository = mockk<AcquirerRepository>()
        val createdAnnulmentsRepositoryPortOut = mockk<CreatedAnnulmentsRepositoryPortOut>()
        val idProvider = mockk<IdProvider>()
        val mapper = mockk<ToCreatedAnnulmentMapper>()

        val useCase = CreateAnnulmentUseCase(
            acquirerRepository = acquireRepository,
            createdAnnulmentsRepository = createdAnnulmentsRepositoryPortOut,
            idProvider = idProvider,
            toCreatedAnnulmentMapper = mapper
        )

        beforeEach { clearAllMocks() }

        scenario("successful annulment creation") {
            val expectedAnnulment = anAnnulment()
            val authorization = anAuthorization()
            val createdAnnulment = aCreatedAnnulment()

            // given mocked dependencies
            every { acquireRepository.authorizeAnnulment(expectedAnnulment) } returns authorization
            every { idProvider.provide() } returns id
            every { mapper.from(expectedAnnulment, authorization, id) } returns createdAnnulment
            every { createdAnnulmentsRepositoryPortOut.save(createdAnnulment) } returns Unit.right()

            // expect that
            useCase.execute(expectedAnnulment) shouldBeRight createdAnnulment

            // dependencies called
            verify(exactly = 1) { acquireRepository.authorizeAnnulment(expectedAnnulment) }
            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { mapper.from(expectedAnnulment, authorization, id) }
            verify(exactly = 1) { createdAnnulmentsRepositoryPortOut.save(createdAnnulment) }
        }

        scenario("failed producing message") {
            val expectedAnnulment = anAnnulment()
            val authorization = anAuthorization()
            val createdAnnulment = aCreatedAnnulment()

            // given mocked dependencies
            every { acquireRepository.authorizeAnnulment(expectedAnnulment) } returns authorization
            every { idProvider.provide() } returns id
            every { mapper.from(expectedAnnulment, authorization, id) } returns createdAnnulment
            every { createdAnnulmentsRepositoryPortOut.save(createdAnnulment) } returns queueProducerNotWritten().left()

            // expect that
            useCase.execute(expectedAnnulment) shouldBeLeft queueProducerNotWritten()

            // dependencies called
            verify(exactly = 1) { acquireRepository.authorizeAnnulment(expectedAnnulment) }
            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { mapper.from(expectedAnnulment, authorization, id) }
            verify(exactly = 1) { createdAnnulmentsRepositoryPortOut.save(createdAnnulment) }
        }
    }
})
