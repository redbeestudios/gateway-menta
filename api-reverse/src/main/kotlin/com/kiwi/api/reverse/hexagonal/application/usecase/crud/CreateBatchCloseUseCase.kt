package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizeBatchCloseUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildBatchCloseUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.hexagonal.domain.BatchClose
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CreateBatchCloseUseCase(
    @Qualifier("provideBatchCloseIdUseCase")
    private val provideIdUseCase: ProvideIdUseCase,
    private val authorizeUseCase: AuthorizeBatchCloseUseCase,
    private val buildUseCase: BuildBatchCloseUseCase
) : CreateBatchClosePortIn {

    override fun execute(request: BatchCloseRequest, merchantId: String): BatchClose =
        with(request) {
            authorize().let {
                createFrom(generateId(), request, it)
            }
        }

    private fun generateId() =
        provideIdUseCase.provide()
            .log { info("id provided: {}", it) }

    private fun BatchCloseRequest.authorize() =
        authorizeUseCase.authorize(this)
            .log { info("batch close authorized: {}", it.authorizationCode) }

    private fun createFrom(id: String, request: BatchCloseRequest, authorization: Authorization) =
        buildUseCase.buildFrom(request, authorization, id)
            .log { info("batch close created: {}", it.id) }

    companion object : CompanionLogger()
}
