package com.menta.apisecrets.adapter.out.repository

import com.menta.apisecrets.adapter.out.SecretRepository
import com.menta.apisecrets.domain.Acquirer.GPS
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.secretNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.core.env.Environment
import java.util.UUID

class SecretRepositorySpec : FeatureSpec({
    val terminal = Terminal(
        serialCode = "terminalSerialCode",
        id = UUID.randomUUID(),
        merchant = Terminal.Merchant(
            id = UUID.randomUUID(),
            customer = Terminal.Merchant.Customer(
                id = UUID.randomUUID()
            )
        )
    )

    val master = "a master"
    val ksn = "a ksn"
    val secret = Secret(master, ksn)
    val baseMasterKey = "secrets"
    val baseKsnKey = "ksn"
    val acquirerKey = { base: String -> "${base}_GPS" }
    val customerKey = { base: String -> "${acquirerKey(base)}_${terminal.merchant.customer.id}" }
    val merchantKey = { base: String -> "${customerKey(base)}_${terminal.merchant.id}" }
    val terminalKey = { base: String -> "${merchantKey(base)}_${terminal.id}" }

    val env = mockk<Environment>()
    val repository = SecretRepository(env)

    beforeEach { clearAllMocks() }

    feature("find secret") {

        scenario("base secret found") {

            // given secret defined at acquirer level
            every { env.getProperty(baseMasterKey) } returns master
            every { env.getProperty(acquirerKey(baseMasterKey)) } returns null
            every { env.getProperty(customerKey(baseMasterKey)) } returns null
            every { env.getProperty(merchantKey(baseMasterKey)) } returns null
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null
            every { env.getProperty(baseKsnKey) } returns ksn

            // repository should return acquirer secret
            repository.execute(terminal, GPS) shouldBeRight secret

            verify(exactly = 1) { env.getProperty(baseMasterKey) }
            verify(exactly = 1) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("acquirer secret found") {

            // given secret defined at acquirer level
            every { env.getProperty(acquirerKey(baseMasterKey)) } returns master
            every { env.getProperty(customerKey(baseMasterKey)) } returns null
            every { env.getProperty(merchantKey(baseMasterKey)) } returns null
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null
            every { env.getProperty(acquirerKey(baseKsnKey)) } returns ksn

            // repository should return acquirer secret
            repository.execute(terminal, GPS) shouldBeRight secret

            verify(exactly = 0) { env.getProperty(baseMasterKey) }
            verify(exactly = 1) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(baseKsnKey) }
            verify(exactly = 1) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("customer secret found") {

            // given secret defined at customer level
            every { env.getProperty(customerKey(baseMasterKey)) } returns master
            every { env.getProperty(merchantKey(baseMasterKey)) } returns null
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null
            every { env.getProperty(customerKey(baseKsnKey)) } returns ksn

            // repository should return customer secret
            repository.execute(terminal, GPS) shouldBeRight secret

            verify(exactly = 0) { env.getProperty(baseMasterKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("merchant secret found") {

            // given secret defined at merchant level
            every { env.getProperty(merchantKey(baseMasterKey)) } returns master
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null
            every { env.getProperty(merchantKey(baseKsnKey)) } returns ksn

            // repository should return merchant secret
            repository.execute(terminal, GPS) shouldBeRight secret

            verify(exactly = 0) { env.getProperty(baseMasterKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("terminal secret found") {

            // given secret defined at terminal level
            every { env.getProperty(terminalKey(baseMasterKey)) } returns master
            every { env.getProperty(terminalKey(baseKsnKey)) } returns ksn

            // repository should return terminal secret
            repository.execute(terminal, GPS) shouldBeRight secret

            verify(exactly = 0) { env.getProperty(baseMasterKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("no secret found") {

            // given a non existent secret
            every { env.getProperty(baseMasterKey) } returns null
            every { env.getProperty(acquirerKey(baseMasterKey)) } returns null
            every { env.getProperty(customerKey(baseMasterKey)) } returns null
            every { env.getProperty(merchantKey(baseMasterKey)) } returns null
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null

            // repository should return terminal secret
            repository.execute(terminal, GPS) shouldBeLeft secretNotFound()

            verify(exactly = 1) { env.getProperty(baseMasterKey) }
            verify(exactly = 1) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 0) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }

        scenario("ksn not found") {

            // given secret defined at acquirer level
            every { env.getProperty(baseMasterKey) } returns master
            every { env.getProperty(acquirerKey(baseMasterKey)) } returns null
            every { env.getProperty(customerKey(baseMasterKey)) } returns null
            every { env.getProperty(merchantKey(baseMasterKey)) } returns null
            every { env.getProperty(terminalKey(baseMasterKey)) } returns null
            every { env.getProperty(baseKsnKey) } returns null

            // repository should return acquirer secret
            repository.execute(terminal, GPS) shouldBeRight secret.copy(ksn = null)

            verify(exactly = 1) { env.getProperty(baseMasterKey) }
            verify(exactly = 1) { env.getProperty(acquirerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(customerKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(merchantKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(terminalKey(baseMasterKey)) }
            verify(exactly = 1) { env.getProperty(baseKsnKey) }
            verify(exactly = 0) { env.getProperty(acquirerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(customerKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(merchantKey(baseKsnKey)) }
            verify(exactly = 0) { env.getProperty(terminalKey(baseKsnKey)) }
        }
    }
})
