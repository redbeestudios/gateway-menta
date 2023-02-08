package com.menta.apisecrets.adapter.out

import arrow.core.Either
import com.menta.apisecrets.adapter.out.SecretKey.Companion.moreGeneric
import com.menta.apisecrets.application.port.out.SecretRepositoryOutPort
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.secretNotFound
import com.menta.apisecrets.shared.util.leftIfReceiverNull
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SecretRepository(
    private val environment: Environment
) : SecretRepositoryOutPort {

    override fun execute(terminal: Terminal, acquirer: Acquirer): Either<ApplicationError, Secret> =
        buildSecretKey(terminal, acquirer)
            .getMostSpecificKey()
            .findMaster()
            .leftIfReceiverNull(secretNotFound())
            .map { it to it.key.findKSN() }
            .map { it.toDomain() }

    private fun buildSecretKey(terminal: Terminal, acquirer: Acquirer) =
        SecretKey(
            acquirerId = acquirer.name,
            customerId = terminal.merchant.customer.id.toString(),
            merchantId = terminal.merchant.id.toString(),
            terminalId = terminal.id.toString()
        ).log { info("secret ket: {}", it) }

    private fun String.findMaster(): Master? =
        getPropertyBy(this)?.let { Master(this, it) }
            ?: moreGeneric(this)?.findMaster()

    private fun String.findKSN(): KSN? =
        getPropertyBy(SecretKey.ksnKeyFrom(this))
            ?.let { KSN(key = this, value = it) }
            .log { info("ksn for master: {} - {}", this, it) }

    private fun getPropertyBy(key: String): String? =
        environment.getProperty(key)
            .log { info("found: {} for key: {}", it, key) }

    private fun Pair<Master, KSN?>.toDomain() =
        Secret(master = first.value, ksn = second?.value)
            .log { info("secret found: {}", it) }

    companion object : CompanionLogger()
}

private data class Master(
    val key: String,
    val value: String
)

private data class KSN(
    val key: String,
    val value: String
)

private data class SecretKey(
    val acquirerId: String,
    val customerId: String,
    val merchantId: String,
    val terminalId: String
) {

    fun getMostSpecificKey() =
        getByTerminal()

    private fun getByAcquirer() =
        getRoot().plusDelimited(acquirerId)

    private fun getByCustomer() =
        getByAcquirer().plusDelimited(customerId)

    private fun getByMerchant() =
        getByCustomer().plusDelimited(merchantId)

    private fun getByTerminal() =
        getByMerchant().plusDelimited(terminalId)

    private fun getRoot() =
        SECRET_MAIN_KEY

    private fun String.plusDelimited(next: String) =
        this + DELIMITER + next

    companion object {
        private const val SECRET_MAIN_KEY = "secrets"
        private const val KSN_MAIN_KEY = "ksn"
        private const val DELIMITER = "_"

        fun moreGeneric(key: String) =
            key.substringBeforeLast(DELIMITER).let {
                if (it == key) {
                    null
                } else {
                    it
                }
            }

        fun ksnKeyFrom(key: String) =
            key.replace(SECRET_MAIN_KEY, KSN_MAIN_KEY)
    }
}
