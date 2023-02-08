package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizeAnnulmentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildAnnulmentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import com.kiwi.api.reverse.hexagonal.domain.Annulment
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CreateAnnulmentUseCase(
    @Qualifier("provideReimbursementIdUseCase")
    private val provideIdUseCase: ProvideIdUseCase,
    private val authorizeUseCase: AuthorizeAnnulmentUseCase,
    private val buildUseCase: BuildAnnulmentUseCase
) : CreateAnnulmentPortIn {

    override fun execute(request: ReimbursementRequest, merchantId: String): Annulment =
        with(request) {
            createFrom(generateId(), request, authorize(), merchantId)
        }

    private fun generateId() =
        provideIdUseCase.provide()
            .log { info("id provided: {}", it) }

    private fun ReimbursementRequest.authorize() =
        authorizeUseCase.authorize(this)
            .log { info("annulment authorized: {}", it.authorizationCode) }

    private fun createFrom(
        id: String,
        request: ReimbursementRequest,
        authorization: Authorization,
        merchantId: String
    ) =
        buildUseCase.buildFrom(request, authorization, id, merchantId)
            .log { info("annulment created: {}", it.id) }

    companion object : CompanionLogger()
}
