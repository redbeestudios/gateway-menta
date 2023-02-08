package com.menta.bff.devices.login.entities.terminal.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.terminals.application.port.out.FindTerminalPortOut
import com.menta.bff.devices.login.entities.terminals.application.service.FindTerminalApplicationService
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindTerminalApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindTerminalPortOut>()

    val findService = FindTerminalApplicationService(
        findTerminal = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find terminal by serialCode") {
        val serialCode = "serialCode"

        scenario("terminal found") {
            val terminal = aTerminal()

            every { findPortOut.findBy(serialCode) } returns terminal.right()

            findService.findBy(serialCode) shouldBeRight terminal

            verify(exactly = 1) { findPortOut.findBy(serialCode) }
        }
        scenario("terminal not found") {
            val error = notFound("terminal $serialCode not found")

            every { findPortOut.findBy(serialCode) } returns error.left()

            findService.findBy(serialCode) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(serialCode) }
        }
    }
})
