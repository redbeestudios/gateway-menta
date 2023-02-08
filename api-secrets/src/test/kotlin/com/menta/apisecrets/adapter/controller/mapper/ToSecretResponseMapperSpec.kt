package com.menta.apisecrets.adapter.controller.mapper

import com.menta.apisecrets.aCustomer
import com.menta.apisecrets.aTerminal
import com.menta.apisecrets.adapter.`in`.controller.mapper.ToSecretResponseMapper
import com.menta.apisecrets.adapter.`in`.controller.model.SecretResponse
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.domain.Secrets.Context
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToSecretResponseMapperSpec : FeatureSpec({

    feature("Map response") {

        val mapper = ToSecretResponseMapper()

        beforeEach {
            clearAllMocks()
        }

        scenario("Successful mapping") {
            val secrets = Secrets(
                options = listOf(Secret(master = "master", ksn = "ksn")),
                context = Context(aCustomer(), aTerminal(), Acquirer.GPS)
            )

            mapper.map(secrets) shouldBe SecretResponse(
                context = SecretResponse.Context(
                    terminal = SecretResponse.Context.Terminal(
                        id = secrets.context.terminal.id,
                        serialCode = secrets.context.terminal.serialCode
                    )
                ),
                secrets = listOf(
                    SecretResponse.Secret(
                        master = secrets.options[0].master,
                        ksn = secrets.options[0].ksn
                    )
                )
            )
        }
    }
})
