package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.right
import com.kiwi.api.reversal.hexagonal.adapter.out.event.CreatedPaymentReversalProducer
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedPaymentMapper
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreatePaymentUseCaseSpec : FeatureSpec({

    feature("payment creation") {

        lateinit var acquirerRepository: AcquirerRepository
        lateinit var mapper: ToCreatedPaymentMapper
        lateinit var createdPaymentProducer: CreatedPaymentReversalProducer

        lateinit var useCase: CreatePaymentUseCase

        beforeEach {
            acquirerRepository = mockk()
            mapper = mockk()
            createdPaymentProducer = mockk()

            useCase = CreatePaymentUseCase(
                acquirerRepository = acquirerRepository,
                toCreatedPaymentMapper = mapper,
                producer = createdPaymentProducer
            )
        }

        scenario("successful payment creation") {
            val eitherRight = Unit.right()
            val authorization = anAuthorization()
            val payment = aPayment()
            val createdPayment = aCreatedPayment()

            // given mocked dependencies
            every { acquirerRepository.authorize(payment) } returns authorization
            every { mapper.map(payment, authorization) } returns createdPayment
            every { createdPaymentProducer.produce(createdPayment) } returns eitherRight

            // expect that
            useCase.execute(payment) shouldBe createdPayment.right()

            // dependencies called
            verify(exactly = 1) { acquirerRepository.authorize(payment) }
            verify(exactly = 1) { mapper.map(payment, authorization) }
        }
    }
})
