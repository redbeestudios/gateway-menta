package com.kiwi.api.payments.application.useCase

import arrow.core.right
import com.kiwi.api.payments.application.port.out.GlobalClientRepository
import com.kiwi.api.payments.application.usecase.PingUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class PingUseCaseSpec : FeatureSpec({

    feature("Ping Acquirer") {

        lateinit var globalClientRepository: GlobalClientRepository

        lateinit var useCase: PingUseCase

        beforeEach {
            globalClientRepository = mockk()

            useCase = PingUseCase(
                globalClientRepository = globalClientRepository
            )
        }

        scenario("Successful ping") {

            // given mocked dependencies
            every { globalClientRepository.ping() } returns Unit.right()

            // expect that
            useCase.execute() shouldBe Unit.right()

            // dependencies called
            verify(exactly = 1) { globalClientRepository.ping() }
        }
    }
})
