package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.aOperationId
import com.kiwi.api.payments.hexagonal.application.aReversalOperaionWithoutOperationId
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import com.kiwi.api.payments.hexagonal.application.port.`in`.ReversePaymentPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.application.port.out.ReverseOperationRepositoryPortOut
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ReversePaymentUseCaseSpec : FeatureSpec({

    feature("Reverse for Payment Use Case") {

        lateinit var reversalPaymentRepository: ReverseOperationRepositoryPortOut
        lateinit var operationRepositoryPortOut: OperationRepositoryPortOut

        lateinit var useCase: ReversePaymentPortIn

        beforeEach {
            reversalPaymentRepository = mockk()
            operationRepositoryPortOut = mockk()

            useCase = ReversePaymentUseCase(
                reversalPaymentRepository = reversalPaymentRepository,
                operationRepositoryPortOut = operationRepositoryPortOut
            )
        }

        scenario("Successful Reverse without Id Operation") {
            val aReversalOperationWithoutOperationId = aReversalOperaionWithoutOperationId()
            val aReversalOperation = aReversalOperation()

            // given mocked dependencies
            every { operationRepositoryPortOut.getId(aReversalOperationWithoutOperationId) } returns aOperationId.right()
            every { reversalPaymentRepository.produce(aReversalOperation) } returns aReversalOperation.right()

            // expect that
            useCase.execute(aReversalOperationWithoutOperationId) shouldBe aReversalOperation.right()

            // dependencies called
            verify(exactly = 1) { operationRepositoryPortOut.getId(aReversalOperationWithoutOperationId) }
            verify(exactly = 1) { reversalPaymentRepository.produce(aReversalOperation) }
        }

        scenario("Successful Reverse with Id Operation") {
            val aReversalOperation = aReversalOperation()

            // given mocked dependencies
            every { reversalPaymentRepository.produce(aReversalOperation) } returns aReversalOperation.right()

            // expect that
            useCase.execute(aReversalOperation) shouldBe aReversalOperation.right()

            // dependencies called
            verify(exactly = 0) { operationRepositoryPortOut.getId(aReversalOperation) }
            verify(exactly = 1) { reversalPaymentRepository.produce(aReversalOperation) }
        }
    }
})
