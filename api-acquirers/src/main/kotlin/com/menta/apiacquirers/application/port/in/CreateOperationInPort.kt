package com.menta.apiacquirers.application.port.`in`

import arrow.core.Either
import com.fasterxml.jackson.databind.JsonNode
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.shared.error.model.ApplicationError
import org.springframework.http.ResponseEntity

interface CreateOperationInPort {
    fun execute(
        operation: JsonNode,
        country: String,
        operationType: OperationType
    ): Either<ApplicationError, ResponseEntity<JsonNode>>
}
