package com.menta.apisecrets.adapter.`in`.controller.mapper

import com.menta.apisecrets.adapter.`in`.controller.model.SecretResponse
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToSecretResponseMapper {
    fun map(secrets: Secrets): SecretResponse =
        with(secrets) {
            SecretResponse(
                secrets = options.map { SecretResponse.Secret(master = it.master, ksn = it.ksn) },
                context = SecretResponse.Context(
                    terminal = SecretResponse.Context.Terminal(
                        id = context.terminal.id,
                        serialCode = context.terminal.serialCode
                    )
                )
            ).log { info("secret response mapped: {}", it) }
        }

    companion object : CompanionLogger()
}
