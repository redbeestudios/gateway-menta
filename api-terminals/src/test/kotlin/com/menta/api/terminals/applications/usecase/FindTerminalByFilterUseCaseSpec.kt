package com.menta.api.terminals.applications.usecase

import com.menta.api.terminals.aSerialCode
import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class FindTerminalByFilterUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalRepositoryOutPort>()
    val useCase = FindTerminalByFilterUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find by serial code") {

        scenario("terminal found") {

            every { repository.findBy(aSerialCode, null, null, null, null, PageRequest.of(0, 10)) } returns PageImpl(
                listOf(
                    aTerminal
                )
            )

            useCase.execute(aSerialCode, null, null, null, null, 0, 10) shouldBeRight PageImpl(listOf(aTerminal))

            verify(exactly = 1) { repository.findBy(aSerialCode, null, null, null, null, PageRequest.of(0, 10)) }
        }

        scenario("terminal NOT found") {

            every {
                repository.findBy(
                    aSerialCode,
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(0, 10)
                )
            } returns PageImpl(emptyList())

            useCase.execute(aSerialCode, null, null, null, null, 0, 10) shouldBeLeft terminalNotFound(
                aSerialCode,
                null,
                null,
                null,
                null
            )

            verify(exactly = 1) { repository.findBy(aSerialCode, null, null, null, null, PageRequest.of(0, 10)) }
        }
    }

    feature("find by customer id") {

        scenario("terminal found") {

            every {
                repository.findBy(
                    null, null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"), null, null, PageRequest.of(0, 10)
                )
            } returns PageImpl(listOf(aTerminal))

            useCase.execute(
                null,
                null,
                UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                null,
                null,
                0,
                10
            ) shouldBeRight PageImpl(listOf(aTerminal))

            verify(exactly = 1) {
                repository.findBy(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    PageRequest.of(0, 10)
                )
            }
        }

        scenario("terminal NOT found") {

            every {
                repository.findBy(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    PageRequest.of(0, 10)
                )
            } returns PageImpl(emptyList())

            useCase.execute(
                null,
                null,
                UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                null,
                null,
                0,
                10
            ) shouldBeLeft terminalNotFound(
                null,
                null,
                UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                null,
                null
            )

            verify(exactly = 1) {
                repository.findBy(
                    null,
                    null,
                    UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
                    null,
                    null,
                    PageRequest.of(0, 10)
                )
            }
        }
    }
})
