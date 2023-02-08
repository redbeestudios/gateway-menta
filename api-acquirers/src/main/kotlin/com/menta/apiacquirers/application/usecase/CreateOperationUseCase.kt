package com.menta.apiacquirers.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.fasterxml.jackson.databind.JsonNode
import com.menta.apiacquirers.application.port.`in`.CreateOperationInPort
import com.menta.apiacquirers.application.port.out.AcquirerRepositoryOutPort
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.domain.provider.AcquirerProvider
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val acquirerProvider: AcquirerProvider,
    private val acquirerRepositoryOutPort: AcquirerRepositoryOutPort
) : CreateOperationInPort {

    override fun execute(
        operation: JsonNode,
        country: String,
        operationType: OperationType
    ): Either<ApplicationError, ResponseEntity<JsonNode>> =
        acquirerProvider.provideBy(country).flatMap {
            acquirerRepositoryOutPort.execute(operation, it, operationType)
        }.logRight { info("operation executed with status: {}", it.statusCode) }

    companion object : CompanionLogger()
}
