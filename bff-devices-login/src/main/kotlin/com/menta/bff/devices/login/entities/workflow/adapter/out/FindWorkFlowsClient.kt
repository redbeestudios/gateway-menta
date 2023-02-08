package com.menta.bff.devices.login.entities.workflow.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.workflow.application.port.out.FindWorkFlowsPortOut
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import com.menta.bff.devices.login.shared.other.error.model.exception.ApplicationErrorException
import com.menta.bff.devices.login.shared.other.util.leftIfReceiverNull
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import com.menta.bff.devices.login.shared.other.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class FindWorkFlowsClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.workflows.url}")
    private val url: String,
    @Value("\${externals.entities.workflows.request-timeout}")
    private val timeout: Long
) : FindWorkFlowsPortOut {

    override fun findBy(email: Email, type: UserType, userAuth: UserAuth): Either<ApplicationError, List<WorkFlow>> =
        log.benchmark("find workflows by $email and $type") {
            try {
                webClient.get()
                    .uri(URI(buildUri(email, type)))
                    .header("Authorization", "Bearer ${userAuth.token?.idToken}")
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("workflows for user $email for $type not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching workflows by $email and $type : {}", it) },
                { info("workflows by $email and $type found : {}", it) }
            )
        }

    private fun buildUri(email: Email, type: UserType) = "$url?email=$email&type=$type"

    private fun Mono<Page>.toDomain() = this.map { it.workflows }

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the workflows in Workflow Api"))
        }

    companion object : CompanionLogger()
}

data class Page(
    val workflows: List<WorkFlow>
)
