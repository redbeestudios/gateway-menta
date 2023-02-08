package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAuthorizationMapper {

    fun map(response: AcquirerResponse): Authorization =
        with(response) {
            Authorization(
                authorizationCode = authorization.code,
                retrievalReferenceNumber = authorization.retrievalReferenceNumber,
                displayMessage = displayMessage?.let {
                    Authorization.DisplayMessage(
                        message = it.message,
                        useCode = it.useCode
                    )
                },
                status = Authorization.Status(
                    code = authorization.status.code,
                    situation = Authorization.Status.Situation(
                        id = authorization.status.situation.id,
                        description = authorization.status.situation.description
                    )
                )
            )
        }.log {
            info("Authorization received from acquirer: {}", it)
        }

    companion object : CompanionLogger()
}
