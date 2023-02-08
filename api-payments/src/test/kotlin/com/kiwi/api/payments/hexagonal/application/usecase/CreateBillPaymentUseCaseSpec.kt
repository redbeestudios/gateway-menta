package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.aCreatedPaymentPending
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.anAuthorization
import com.kiwi.api.payments.hexagonal.application.emvCreatedPayment
import com.kiwi.api.payments.hexagonal.application.emvPayment
import com.kiwi.api.payments.hexagonal.application.mapper.ToCreatedPaymentMapper
import com.kiwi.api.payments.hexagonal.application.port.out.AcquirerRepositoryPortOut
import com.kiwi.api.payments.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.payments.hexagonal.application.usecase.provider.AuthorizationPendingProvider
import com.kiwi.api.payments.hexagonal.domain.Origin.BILL
import com.kiwi.api.payments.hexagonal.domain.StatusCode.PENDING
import com.kiwi.api.payments.shared.error.model.AcquirerTimeOutError
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateBillPaymentUseCaseSpec : FeatureSpec({

    feature("payment creation") {

        lateinit var acquirerRepository: AcquirerRepositoryPortOut
        lateinit var createdPaymentProducer: CreatedPaymentProducerPortOut
        lateinit var mapper: ToCreatedPaymentMapper
        lateinit var authorizationPendingProvider: AuthorizationPendingProvider

        lateinit var useCase: CreateBillPaymentUseCase

        beforeEach {
            acquirerRepository = mockk()
            createdPaymentProducer = mockk()
            mapper = mockk()
            authorizationPendingProvider = mockk()

            useCase = CreateBillPaymentUseCase(
                acquirerRepository = acquirerRepository,
                createdPaymentProducer = createdPaymentProducer,
                createdPaymentMapper = mapper,
                authorizationPendingProvider = authorizationPendingProvider
            )
        }

        scenario("successful payment creation") {
            val eitherRight = Unit.right()
            val createdPayment = aCreatedPayment().copy(origin = BILL)
            val payment = aPayment()
            val authorization = anAuthorization()

            // given mocked dependencies
            every { acquirerRepository.authorize(payment) } returns authorization.right()
            every { mapper.mapFromBillPayment(payment, authorization) } returns createdPayment
            every { createdPaymentProducer.produce(createdPayment) } returns eitherRight

            // expect that
            useCase.execute(payment) shouldBe createdPayment.right()

            // dependencies called

            verify(exactly = 0) { authorizationPendingProvider.provide() }
            verify(exactly = 1) { acquirerRepository.authorize(payment) }
            verify(exactly = 1) { mapper.mapFromBillPayment(payment, authorization) }
            verify(exactly = 1) { createdPaymentProducer.produce(createdPayment) }
        }

        scenario("successful payment creation when input mode is emv") {
            val eitherRight = Unit.right()
            val createdPayment = emvCreatedPayment().copy(origin = BILL)
            val payment = emvPayment()
            val authorization = anAuthorization()

            // given mocked dependencies
            every { acquirerRepository.authorize(payment) } returns authorization.right()
            every { mapper.mapFromBillPayment(payment, authorization) } returns createdPayment
            every { createdPaymentProducer.produce(createdPayment) } returns eitherRight

            // expect that
            useCase.execute(payment) shouldBe createdPayment.right()

            // dependencies called
            verify(exactly = 0) { authorizationPendingProvider.provide() }
            verify(exactly = 1) { acquirerRepository.authorize(payment) }
            verify(exactly = 1) { mapper.mapFromBillPayment(payment, authorization) }
            verify(exactly = 1) { createdPaymentProducer.produce(createdPayment) }
        }

        scenario("Acquirer Timeout Error, payment save with Authorization Pending") {
            val eitherRight = Unit.right()
            val createdPayment = aCreatedPaymentPending()
            val payment = aPayment()
            val authorizationPending = anAuthorization(PENDING)
            val acquirerError = AcquirerTimeOutError()

            // given mocked dependencies
            every { acquirerRepository.authorize(payment) } returns acquirerError.left()
            every { authorizationPendingProvider.provide() } returns authorizationPending
            every { mapper.mapFromBillPayment(payment, authorizationPending) } returns createdPayment
            every { createdPaymentProducer.produce(createdPayment) } returns eitherRight

            // expect that
            useCase.execute(payment) shouldBe acquirerError.left()

            // dependencies called
            verify(exactly = 1) { authorizationPendingProvider.provide() }
            verify(exactly = 1) { acquirerRepository.authorize(payment) }
            verify(exactly = 1) { mapper.mapFromBillPayment(payment, authorizationPending) }
            verify(exactly = 1) { createdPaymentProducer.produce(createdPayment) }
        }
    }
})
