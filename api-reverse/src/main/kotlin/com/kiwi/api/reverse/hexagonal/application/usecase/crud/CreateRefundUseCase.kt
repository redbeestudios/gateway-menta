package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizeRefundUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildRefundUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.hexagonal.domain.Refund
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CreateRefundUseCase(
    @Qualifier("provideReimbursementIdUseCase")
    private val provideIdUseCase: ProvideIdUseCase,
    private val authorizeUseCase: AuthorizeRefundUseCase,
    private val buildUseCase: BuildRefundUseCase
) : CreateRefundPortIn {

    override fun execute(request: ReimbursementRequest, merchantId: String): Refund =
        with(request) {
            createFrom(generateId(), request, authorize())
        }

    private fun generateId() =
        provideIdUseCase.provide()
            .log { info("id provided: {}", it) }

    private fun ReimbursementRequest.authorize() =
        authorizeUseCase.authorize(this)
            .log { info("refund authorized: {}", it.authorizationCode) }

    private fun createFrom(id: String, request: ReimbursementRequest, authorization: Authorization) =
        buildUseCase.buildFrom(request, authorization, id)
            .log { info("refund created: {}", it.id) }

    companion object : CompanionLogger()
}
