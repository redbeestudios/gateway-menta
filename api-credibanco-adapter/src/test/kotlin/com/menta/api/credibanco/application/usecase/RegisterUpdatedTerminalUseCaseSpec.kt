package com.menta.api.credibanco.application.usecase

import arrow.core.None
import arrow.core.Some
import com.menta.api.credibanco.aTerminalId
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.error.model.TerminalAlreadyRegistered
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class RegisterUpdatedTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalsRepository>()
    val useCase = RegisterUpdatedTerminalUseCase(repository)

    beforeEach {
        clearAllMocks()
    }

    feature("execute") {

        scenario("success registration") {

            every { repository.register(aTerminalId) } returns Some(aTerminalId)

            useCase.execute(aTerminalId) shouldBeRight aTerminalId
        }

        scenario("unsuccessful registration") {

            every { repository.register(aTerminalId) } returns None

            useCase.execute(aTerminalId) shouldBeLeft TerminalAlreadyRegistered(aTerminalId)
        }
    }
})
