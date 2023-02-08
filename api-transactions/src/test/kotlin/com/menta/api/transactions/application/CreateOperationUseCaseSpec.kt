package com.menta.api.transactions.application

import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.usecase.CreateOperationUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateOperationUseCaseSpec : FeatureSpec({
    feature("create operation") {
        lateinit var operationRepository: OperationRepositoryOutPort
        lateinit var useCase: CreateOperationUseCase

        beforeEach {
            operationRepository = mockk()
            useCase = CreateOperationUseCase(
                operationRepository = operationRepository
            )
        }
        scenario("successful create") {
            val transaction = aTransaction(UUID.fromString(TRANSACTION_ID))

            // given mocked dependencies
            every { operationRepository.create(transaction) } returns Unit

            // expect that
            useCase.execute(transaction)

            // dependencies called
            verify(exactly = 1) { operationRepository.create(transaction) }
        }
    }
})
