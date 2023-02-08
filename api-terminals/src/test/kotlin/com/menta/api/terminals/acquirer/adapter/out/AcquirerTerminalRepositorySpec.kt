package com.menta.api.terminals.acquirer.adapter.out

import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.acquirer.anAcquirerId
import com.menta.api.terminals.acquirer.anAcquirerTerminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class AcquirerTerminalRepositorySpec : FeatureSpec({

    val dbRepository = mockk<AcquirerTerminalDbRepository>()
    val repository = AcquirerTerminalRepository(dbRepository)

    beforeEach { clearAllMocks() }

    feature("find by acquirer terminal id") {

        scenario("terminal found") {

            every { dbRepository.findByAcquirerAndTerminalId(anAcquirerId, aTerminalId) } returns Optional.of(
                anAcquirerTerminal
            )

            repository.findBy(anAcquirerId, aTerminalId) shouldBe Optional.of(anAcquirerTerminal)

            verify(exactly = 1) { dbRepository.findByAcquirerAndTerminalId(anAcquirerId, aTerminalId) }
        }

        scenario("terminal NOT found") {

            every { dbRepository.findByAcquirerAndTerminalId(anAcquirerId, aTerminalId) } returns Optional.empty()

            repository.findBy(anAcquirerId, aTerminalId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByAcquirerAndTerminalId(anAcquirerId, aTerminalId) }
        }
    }
})
