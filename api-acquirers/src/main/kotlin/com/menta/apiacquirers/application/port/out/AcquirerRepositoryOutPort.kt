package com.menta.apiacquirers.application.port.out

import arrow.core.Either
import com.fasterxml.jackson.databind.JsonNode
import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.shared.error.model.ApplicationError
import org.springframework.http.ResponseEntity

interface AcquirerRepositoryOutPort {
    fun execute(
        operation: JsonNode,
        acquirer: OperableAcquirers.Acquirer,
        operationType: OperationType
    ): Either<ApplicationError, ResponseEntity<JsonNode>>
}
