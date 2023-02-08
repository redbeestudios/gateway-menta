package com.menta.apisecrets.domain

import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

data class Secret(
    val master: String,
    val ksn: String? = null
)

data class Secrets(
    val options: List<Secret>,
    val context: Context
) {
    data class Context(
        val customer: Customer,
        val terminal: Terminal,
        val acquirer: Acquirer
    )
}

@Component
class SecretsProvider {

    fun provide(secrets: List<Secret>, customer: Customer, terminal: Terminal, acquirer: Acquirer) =
        Secrets(
            options = secrets,
            context = Secrets.Context(
                customer = customer,
                terminal = terminal,
                acquirer = acquirer
            )
        ).log { info("secrets provided") }

    companion object : CompanionLogger()
}
