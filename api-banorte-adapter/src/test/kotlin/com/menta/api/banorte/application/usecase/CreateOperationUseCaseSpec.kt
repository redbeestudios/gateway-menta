package com.menta.api.banorte.application.usecase

import arrow.core.right
import com.menta.api.banorte.application.aCreatedOperation
import com.menta.api.banorte.application.anOperation
import com.menta.api.banorte.application.port.out.BanorteClientRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOperationUseCaseSpec : FeatureSpec({

    feature("operation creation") {

        lateinit var banorteClientRepository: BanorteClientRepository

        lateinit var useCase: CreateOperationUseCase

        beforeEach {
            banorteClientRepository = mockk()

            useCase = CreateOperationUseCase(
                banorteClientRepository = banorteClientRepository
            )
        }

        scenario("successful operation creation") {
            // given
            val createdOperation = aCreatedOperation().right()
            val operation = anOperation()

            // when
            every { banorteClientRepository.authorize(operation) } returns createdOperation

            // then
            useCase.execute(operation) shouldBe createdOperation

            verify(exactly = 1) { banorteClientRepository.authorize(operation) }
        }
    }
})
