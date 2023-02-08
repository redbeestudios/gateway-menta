package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import com.kiwi.api.payments.hexagonal.application.port.`in`.UpdateStatusOperationPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UpdateStatusOperationUseCaseSpec : FeatureSpec({

    lateinit var repository: OperationRepositoryPortOut
    lateinit var useCase: UpdateStatusOperationPortIn

    beforeEach {
        repository = mockk()
        useCase = UpdateStatusOperationUseCase(
            operationRepository = repository
        )
    }

    feature("Update reversal operation") {
        scenario("successful create") {
            val reversalOperation = aReversalOperation()

            // given mocked dependencies
            every { repository.updateStatusForReversal(reversalOperation) } returns Unit.right()

            // expect that
            useCase.execute(reversalOperation) shouldBe Unit.right()

            // dependencies called
            verify(exactly = 1) { repository.updateStatusForReversal(reversalOperation) }
        }
    }
})
