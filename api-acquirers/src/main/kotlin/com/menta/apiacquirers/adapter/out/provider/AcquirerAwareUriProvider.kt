package com.menta.apiacquirers.adapter.out.provider

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.menta.apiacquirers.adapter.out.AcquirerPathProvider
import com.menta.apiacquirers.adapter.out.OperationPathProvider
import com.menta.apiacquirers.domain.OperableAcquirers.Acquirer
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Component
class AcquirerAwareUriProvider(
    @Value("\${externals.host}")
    private val host: String,
    @Value("\${externals.scheme}")
    private val scheme: String,
    private val acquirerPathProvider: AcquirerPathProvider,
    private val operationPathProvider: OperationPathProvider
) {

    fun provideFor(acquirer: Acquirer, operationType: OperationType): Either<ApplicationError, URI> =
        acquirer.getPath().flatMap { acquirerPath ->
            operationType.getPath().flatMap { operationPath ->
                baseBuilder()
                    .path(acquirerPath + operationPath)
                    .build()
                    .toUri().right()
            }
        }

    private fun baseBuilder() =
        UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)

    private fun Acquirer.getPath() =
        acquirerPathProvider.provideBy(name)
            .logRight { info("acquirer path found: {}", it) }

    private fun OperationType.getPath() =
        operationPathProvider.provideBy(this)
            .logRight { info("acquirer path operation: {}", it) }

    companion object : CompanionLogger()
}
