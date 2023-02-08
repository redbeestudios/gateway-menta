package com.menta.apisecrets.adapter.`in`.controller

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.menta.apisecrets.adapter.`in`.controller.mapper.ToSecretResponseMapper
import com.menta.apisecrets.adapter.`in`.controller.model.SecretResponse
import com.menta.apisecrets.application.port.`in`.FindSecretInPort
import com.menta.apisecrets.application.port.out.TerminalUpdateProducerOutPort
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.providers.ErrorResponseProvider
import com.menta.apisecrets.shared.util.log.CompanionLogger
import com.menta.apisecrets.shared.util.log.benchmark
import com.menta.apisecrets.shared.util.rest.evaluate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/public")
class SecretsController(
    private val findSecretInPort: FindSecretInPort,
    private val toSecretResponseMapper: ToSecretResponseMapper,
    private val terminalUpdateProducerInPort: TerminalUpdateProducerOutPort,
    private val errorResponseProvider: ErrorResponseProvider
) {

    @GetMapping("/terminals/{terminal}/secrets")
    fun get(@PathVariable terminal: String) =
        log.benchmark("get secret") {
            findSecret(terminal)
                .sendUpdatedTerminalSecretsEvent()
                .map { it.toResponse() }
                .evaluate(HttpStatus.OK) { errorResponseProvider.provideFor(this) }
        }

    private fun findSecret(terminal: String) =
        findSecretInPort.execute(terminal)

    private fun Either<ApplicationError, Secrets>.sendUpdatedTerminalSecretsEvent() =
        flatMap { secrets ->
            terminalUpdateProducerInPort.produce(secrets)
            secrets.right()
        }

    private fun Secrets.toResponse(): SecretResponse =
        toSecretResponseMapper.map(this)
            .log { info("response built: {}", it) }

    companion object : CompanionLogger()
}
