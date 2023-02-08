package com.menta.apisecrets.domain

import com.menta.apisecrets.aCustomer
import com.menta.apisecrets.aSecret
import com.menta.apisecrets.aTerminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class SecretsProviderSpec : FeatureSpec({

    feature("provide secrets") {

        scenario("secret provided") {

            val secrets = listOf(aSecret())
            val customer = aCustomer()
            val terminal = aTerminal()
            val acquirer = Acquirer.GPS

            SecretsProvider().provide(secrets, customer, terminal, acquirer) shouldBe
                Secrets(
                    options = secrets,
                    context = Secrets.Context(
                        customer = customer,
                        terminal = terminal,
                        acquirer = acquirer
                    )
                )
        }
    }
})
