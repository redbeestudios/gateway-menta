package com.menta.api.credibanco.application.usecase

import com.menta.api.credibanco.aTerminal
import com.menta.api.credibanco.aTerminalId
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.error.model.OutdatedTerminal
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ValidateTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalsRepository>()
    val useCase = ValidateTerminalUseCase(repository)

    beforeEach {
        clearAllMocks()
    }

    feature("execute") {

        scenario("successful validation") {

            every { repository.exists(aTerminalId) } returns true

            useCase.execute(aTerminal) shouldBeRight  true
        }

        scenario("unsuccessful validation") {

            every { repository.exists(aTerminalId) } returns false

            useCase.execute(aTerminal) shouldBeLeft OutdatedTerminal(aTerminal.serialCode)
        }
    }
})
