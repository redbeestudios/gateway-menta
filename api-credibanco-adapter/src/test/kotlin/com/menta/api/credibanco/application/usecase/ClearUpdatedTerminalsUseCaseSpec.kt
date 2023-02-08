package com.menta.api.credibanco.application.usecase

import com.menta.api.credibanco.anAcquirer
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.error.model.NoTerminalsRegistered
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ClearUpdatedTerminalsUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalsRepository>()
    val useCase = ClearUpdatedTerminalsUseCase(repository)

    beforeEach {
        clearAllMocks()
    }

    feature("execute") {

        scenario("successful key deletion") {

            every { repository.deleteAll() } returns true

            useCase.execute(anAcquirer.name) shouldBeRight true
        }

        scenario("unsuccessful key deletion") {

            every { repository.deleteAll() } returns false

            useCase.execute(anAcquirer.name) shouldBeLeft NoTerminalsRegistered(anAcquirer.name)
        }
    }
})
