package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAuthorizationMapper {

    fun map(response: AcquirerResponse): Authorization =
        with(response) {
            Authorization(
                authorizationCode = authorization.code,
                retrievalReferenceNumber = authorization.retrievalReferenceNumber,
                status = Authorization.Status(
                    code = authorization.status.code,
                    situation = Authorization.Status.Situation(
                        id = authorization.status.situation.id,
                        description = authorization.status.situation.description
                    )
                ),
                displayMessage = displayMessage?.message
            )
        }.log { info("authorization mapped: {}", it) }

    companion object : CompanionLogger()
}
