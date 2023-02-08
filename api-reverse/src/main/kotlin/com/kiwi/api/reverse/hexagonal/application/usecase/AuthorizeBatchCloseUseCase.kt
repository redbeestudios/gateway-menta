package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class AuthorizeBatchCloseUseCase {

    fun authorize(request: BatchCloseRequest) =
        Authorization(
            authorizationCode = UUID.randomUUID().toString(),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = "unoo",
            status = Authorization.Status(
                code = "APPROVED"
            )
        )
            .log { info("batch close authorized: {}", it) }

    companion object : CompanionLogger()
}
