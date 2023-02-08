package com.menta.apisecrets.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.apisecrets.aCustomer
import com.menta.apisecrets.aSecrets
import com.menta.apisecrets.aTerminal
import com.menta.apisecrets.application.port.out.AcquirerRepositoryOutPort
import com.menta.apisecrets.application.port.out.CustomerRepositoryOutPort
import com.menta.apisecrets.application.port.out.SecretRepositoryOutPort
import com.menta.apisecrets.application.port.out.TerminalRepositoryOutPort
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.SecretsProvider
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.customerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindSecretUseCaseSpec : FeatureSpec({
    feature("find secret") {

        val terminalRepository = mockk<TerminalRepositoryOutPort>()
        val customerRepository = mockk<CustomerRepositoryOutPort>()
        val acquirerRepository = mockk<AcquirerRepositoryOutPort>()
        val secretRepository = mockk<SecretRepositoryOutPort>()
        val provider = mockk<SecretsProvider>()
        val useCase = FindSecretUseCase(
            terminalRepository = terminalRepository,
            acquirerRepository = acquirerRepository,
            customerRepository = customerRepository,
            secretRepository = secretRepository,
            secretsProvider = provider
        )

        beforeEach {
            clearAllMocks()
        }

        scenario("secret found") {
            val serialCode = "a serial code"
            val terminal = aTerminal()
            val customer = aCustomer()
            val acquirer = Acquirer.FEENICIA
            val secret = Secret(master = "master", ksn = "ksn")
            val secrets = aSecrets

            every { terminalRepository.execute(serialCode) } returns listOf(terminal).right()
            every { customerRepository.findBy(terminal.merchant.customer.id) } returns customer.right()
            every { acquirerRepository.findBy(customer.country) } returns acquirer.right()
            every { secretRepository.execute(terminal, acquirer) } returns secret.right()
            every { provider.provide(listOf(secret), customer, terminal, acquirer) } returns secrets

            useCase.execute(serialCode) shouldBeRight secrets

            verify(exactly = 1) { terminalRepository.execute(serialCode) }
            verify(exactly = 1) { customerRepository.findBy(terminal.merchant.customer.id) }
            verify(exactly = 1) { acquirerRepository.findBy(customer.country) }
            verify(exactly = 1) { secretRepository.execute(terminal, acquirer) }
            verify(exactly = 1) { provider.provide(listOf(secret), customer, terminal, acquirer) }
        }

        scenario("acquirer not found") {
            val serialCode = "a serial code"
            val terminal = aTerminal()
            val customer = aCustomer()
            val acquirerError = acquirerNotFound()
            every { terminalRepository.execute(serialCode) } returns listOf(terminal).right()
            every { customerRepository.findBy(terminal.merchant.customer.id) } returns customer.right()
            every { acquirerRepository.findBy(customer.country) } returns acquirerError.left()

            useCase.execute(serialCode) shouldBeLeft acquirerError

            verify(exactly = 1) { terminalRepository.execute(serialCode) }
            verify(exactly = 1) { customerRepository.findBy(terminal.merchant.customer.id) }
            verify(exactly = 1) { acquirerRepository.findBy(customer.country) }
            verify(exactly = 0) { secretRepository.execute(any(), any()) }
            verify(exactly = 0) { provider.provide(any(), any(), any(), any()) }
        }

        scenario("customer not found") {
            val serialCode = "a serial code"
            val terminal = aTerminal()
            val customerError = customerNotFound(terminal.merchant.customer.id)
            every { terminalRepository.execute(serialCode) } returns listOf(terminal).right()
            every { customerRepository.findBy(terminal.merchant.customer.id) } returns customerError.left()

            useCase.execute(serialCode) shouldBeLeft customerError

            verify(exactly = 1) { terminalRepository.execute(serialCode) }
            verify(exactly = 1) { customerRepository.findBy(terminal.merchant.customer.id) }
            verify(exactly = 0) { acquirerRepository.findBy(any()) }
            verify(exactly = 0) { secretRepository.execute(any(), any()) }
            verify(exactly = 0) { provider.provide(any(), any(), any(), any()) }
        }

        scenario("terminal not found") {
            val serialCode = "a serial code"
            val terminalError = ApplicationError.terminalNotFound(serialCode)
            every { terminalRepository.execute(serialCode) } returns terminalError.left()

            useCase.execute(serialCode) shouldBeLeft terminalError

            verify(exactly = 1) { terminalRepository.execute(serialCode) }
            verify(exactly = 0) { customerRepository.findBy(any()) }
            verify(exactly = 0) { acquirerRepository.findBy(any()) }
            verify(exactly = 0) { secretRepository.execute(any(), any()) }
            verify(exactly = 0) { provider.provide(any(), any(), any(), any()) }
        }

        scenario("secret not found") {
            val serialCode = "a serial code"
            val terminal = aTerminal()
            val customer = aCustomer()
            val acquirer = Acquirer.FEENICIA
            val secretError = acquirerNotFound()
            every { terminalRepository.execute(serialCode) } returns listOf(terminal).right()
            every { customerRepository.findBy(terminal.merchant.customer.id) } returns customer.right()
            every { acquirerRepository.findBy(customer.country) } returns acquirer.right()
            every { secretRepository.execute(terminal, acquirer) } returns secretError.left()

            useCase.execute(serialCode) shouldBeLeft secretError

            verify(exactly = 1) { terminalRepository.execute(serialCode) }
            verify(exactly = 1) { customerRepository.findBy(terminal.merchant.customer.id) }
            verify(exactly = 1) { acquirerRepository.findBy(customer.country) }
            verify(exactly = 1) { secretRepository.execute(terminal, acquirer) }
            verify(exactly = 0) { provider.provide(any(), any(), any(), any()) }
        }
    }
})
