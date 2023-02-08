package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.port.out.OperationRepositoryOutPort
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOperationUseCaseSpec : FeatureSpec({

    val repository = mockk<OperationRepositoryOutPort>()
    val useCase = CreateOperationUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("create payment operation") {
        scenario("successful create") {
            val message = aCreatedPayment()

            // given mocked dependencies
            every { repository.create(message) } returns Unit

            // expect that
            useCase.execute(message)

            // dependencies called
            verify(exactly = 1) { repository.create(message) }
        }
    }

    feature("create refund operation") {
        scenario("successful create") {
            val message = aCreatedRefund()

            // given mocked dependencies
            every { repository.create(message) } returns Unit

            // expect that
            useCase.execute(message)

            // dependencies called
            verify(exactly = 1) { repository.create(message) }
        }
    }

    feature("create annulment operation") {
        scenario("successful create") {
            val message = aCreatedAnnulment()

            // given mocked dependencies
            every { repository.create(message) } returns Unit

            // expect that
            useCase.execute(message)

            // dependencies called
            verify(exactly = 1) { repository.create(message) }
        }
    }
})
