package com.kiwi.api.payments.hexagonal.application.usecase

import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOperationUseCaseSpec : FeatureSpec({

    val repository = mockk<OperationRepositoryPortOut>()
    val useCase = CreateOperationUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("create operation") {
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
})
