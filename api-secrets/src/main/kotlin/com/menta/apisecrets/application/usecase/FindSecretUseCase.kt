package com.menta.apisecrets.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.apisecrets.application.port.`in`.FindSecretInPort
import com.menta.apisecrets.application.port.out.AcquirerRepositoryOutPort
import com.menta.apisecrets.application.port.out.CustomerRepositoryOutPort
import com.menta.apisecrets.application.port.out.SecretRepositoryOutPort
import com.menta.apisecrets.application.port.out.TerminalRepositoryOutPort
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Country
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.domain.SecretsProvider
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindSecretUseCase(
    private val terminalRepository: TerminalRepositoryOutPort,
    private val customerRepository: CustomerRepositoryOutPort,
    private val acquirerRepository: AcquirerRepositoryOutPort,
    private val secretRepository: SecretRepositoryOutPort,
    private val secretsProvider: SecretsProvider
) : FindSecretInPort {

    override fun execute(terminalSerialCode: String): Either<ApplicationError, Secrets> =
        getTerminalBy(terminalSerialCode).flatMap { terminal ->
            getCustomerBy(terminal.merchant.customer.id).flatMap { customer ->
                getAcquirerBy(customer.country).flatMap { acquirer ->
                    findSecretsBy(terminal, acquirer).map {
                        buildSecrets(it, customer, terminal, acquirer)
                    }
                }
            }
        }

    private fun getTerminalBy(serialCode: String) =
        terminalRepository.execute(serialCode)
            .map { it.first() }
            .logRight { info("terminal found: {}", it) }

    private fun getCustomerBy(id: UUID) =
        customerRepository.findBy(id)
            .logRight { info("customer found: {}", it) }

    private fun getAcquirerBy(country: Country) =
        acquirerRepository.findBy(country)
            .logRight { info("acquirer found: {}", it) }

    private fun findSecretsBy(terminal: Terminal, acquirer: Acquirer) =
        secretRepository.execute(terminal, acquirer).map { listOf(it) }
            .logRight { info("secret found: {}", it) }

    private fun buildSecrets(secrets: List<Secret>, customer: Customer, terminal: Terminal, acquirer: Acquirer) =
        secretsProvider.provide(secrets, customer, terminal, acquirer)
            .log { info("secrets build") }

    companion object : CompanionLogger()
}
