package com.menta.api.credibanco.application.usecase

import arrow.core.right
import com.menta.api.credibanco.aCreatedOperation
import com.menta.api.credibanco.anOperation
import com.menta.api.credibanco.application.port.out.CredibancoRepository
import com.menta.api.credibanco.application.port.out.OperationRepositoryPortOut
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOperationUseCaseSpec : FeatureSpec({

    val repository: CredibancoRepository = mockk()
    val operationRepository: OperationRepositoryPortOut = mockk()

    val usecase = CreateOperationUseCase(repository, operationRepository)

    beforeEach { clearAllMocks() }

    feature("create operation") {
        scenario("execute operation") {
            val createdOperation = aCreatedOperation()

            every { repository.authorize(anOperation) } returns createdOperation.right()
            every { operationRepository.save(createdOperation) } returns Unit

            usecase.execute(anOperation) shouldBeRight aCreatedOperation()

            verify(exactly = 1) { repository.authorize(anOperation) }
            verify(exactly = 1) { operationRepository.save(createdOperation) }
        }
    }
})
