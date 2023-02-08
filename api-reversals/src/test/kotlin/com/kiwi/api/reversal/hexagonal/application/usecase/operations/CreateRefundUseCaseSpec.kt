package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedRefundMapper
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedRefundProducerPortOut
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateRefundUseCaseSpec : FeatureSpec({

    feature("refund creation") {

        val acquirerRepository = mockk<AcquirerRepository>()
        val mapper = mockk<ToCreatedRefundMapper>()
        val producer = mockk<CreatedRefundProducerPortOut>()
        val useCase = CreateRefundUseCase(
            acquirerRepository = acquirerRepository,
            toCreatedRefundMapper = mapper,
            producer = producer
        )

        beforeEach { clearAllMocks() }

        scenario("successful refund creation") {

            val request = aRefund()
            val authorization = anAuthorization()
            val expectedCreatedRefunds = aCreatedRefund()

            // given mocked dependencies
            every { acquirerRepository.authorize(request) } returns authorization
            every { mapper.map(request, authorization) } returns expectedCreatedRefunds
            every { producer.produce(expectedCreatedRefunds) } returns Unit.right()

            // expect that
            useCase.execute(request) shouldBeRight expectedCreatedRefunds

            // dependencies called
            verify(exactly = 1) { acquirerRepository.authorize(request) }
            verify(exactly = 1) { mapper.map(request, authorization) }
            verify(exactly = 1) { producer.produce(expectedCreatedRefunds) }
        }

        scenario("failed producing message") {

            val request = aRefund()
            val authorization = anAuthorization()
            val expectedCreatedRefunds = aCreatedRefund()
            val error = QueueProducerNotWritten()

            // given mocked dependencies
            every { acquirerRepository.authorize(request) } returns authorization
            every { mapper.map(request, authorization) } returns expectedCreatedRefunds
            every { producer.produce(expectedCreatedRefunds) } returns error.left()

            // expect that
            useCase.execute(request) shouldBeLeft error

            // dependencies called
            verify(exactly = 1) { acquirerRepository.authorize(request) }
            verify(exactly = 1) { mapper.map(request, authorization) }
            verify(exactly = 1) { producer.produce(expectedCreatedRefunds) }
        }
    }
})
