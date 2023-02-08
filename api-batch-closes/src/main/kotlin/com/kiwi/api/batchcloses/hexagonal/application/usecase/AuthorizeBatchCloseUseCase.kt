package com.kiwi.api.batchcloses.hexagonal.application.usecase

import com.kiwi.api.batchcloses.hexagonal.domain.Authorization
import com.kiwi.api.batchcloses.shared.util.log.CompanionLogger
import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

@Component
class AuthorizeBatchCloseUseCase {

    fun authorize(request: BatchCloseRequest) =
        Authorization(
            authorizationCode = UUID.randomUUID().toString(),
            datetime = OffsetDateTime.now(),
            status = Authorization.Status(code = "APPROVED"),
            retrievalReferenceNumber = "123ASD",
            displayMessage = "This is a message"
        )
            .log { info("batch close authorized: {}", it) }

    companion object : CompanionLogger()
}
