package com.kiwi.api.payments.hexagonal.application.usecase.provider

import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.Authorization.Status
import com.kiwi.api.payments.hexagonal.domain.StatusCode.PENDING
import org.springframework.stereotype.Component

@Component
class AuthorizationPendingProvider {

    fun provide(): Authorization =
        Authorization(
            status = Status(
                code = PENDING
            )
        )
}
