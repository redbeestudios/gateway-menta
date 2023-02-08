package com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerResponse
import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.Authorization.Status
import com.kiwi.api.payments.hexagonal.domain.Authorization.Status.Situation
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAuthorizationMapper {

    fun map(response: AcquirerResponse) =
        with(response) {
            Authorization(
                authorizationCode = authorization.code,
                retrievalReferenceNumber = authorization.retrievalReferenceNumber,
                status = Status(
                    code = authorization.status.code,
                    situation = Situation(
                        id = authorization.status.situation.id,
                        description = authorization.status.situation.description
                    )
                ),
                displayMessage = displayMessage?.message
            )
        }.log { info("authorization mapped: {}", it) }

    companion object : CompanionLogger()
}
