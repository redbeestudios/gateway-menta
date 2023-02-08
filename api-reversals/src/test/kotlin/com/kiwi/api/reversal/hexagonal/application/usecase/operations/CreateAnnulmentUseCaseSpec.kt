package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.right
import com.kiwi.api.reversal.hexagonal.adapter.out.event.CreatedAnnulmentReversalProducer
import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.hexagonal.application.anAnnulment
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedAnnulmentMapper
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateAnnulmentUseCaseSpec : FeatureSpec({

    feature("annulment creation") {

        lateinit var acquirerRepository: AcquirerRepository
        lateinit var mapper: ToCreatedAnnulmentMapper
        lateinit var producer: CreatedAnnulmentReversalProducer

        lateinit var useCase: CreateAnnulmentUseCase

        beforeEach {
            acquirerRepository = mockk()
            mapper = mockk()
            producer = mockk()

            useCase = CreateAnnulmentUseCase(
                acquirerRepository = acquirerRepository,
                toCreatedAnnulmentMapper = mapper,
                producer = producer
            )
        }

        scenario("successful annulment creation") {
            val eitherRight = Unit.right()
            val annulment = anAnnulment()
            val createdAnnulment = aCreatedAnnulment()
            val authorization = anAuthorization()

            // given mocked dependencies
            every { acquirerRepository.authorize(annulment) } returns authorization
            every { mapper.map(annulment, authorization) } returns createdAnnulment
            every { producer.produce(createdAnnulment) } returns eitherRight

            // expect that
            useCase.execute(annulment) shouldBe createdAnnulment.right()

            // dependencies called
            verify(exactly = 1) { acquirerRepository.authorize(annulment) }
            verify(exactly = 1) { mapper.map(annulment, authorization) }
        }
    }
})
