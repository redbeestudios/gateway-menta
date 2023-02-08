package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class AuthorizeAnnulmentUseCase {

    fun authorize(request: ReimbursementRequest) =
        Authorization(
            authorizationCode = UUID.randomUUID().toString(),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = "unoo",
            status = Authorization.Status(
                code = "APPROVED"
            )
        )
            .log { info("annulment authorized: {}", it) }

    companion object : CompanionLogger()
}
