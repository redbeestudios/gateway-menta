package com.kiwi.api.batchcloses.hexagonal.application.usecase.crud

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.batchcloses.hexagonal.application.usecase.AuthorizeBatchCloseUseCase
import com.kiwi.api.batchcloses.hexagonal.application.usecase.BuildBatchCloseUseCase
import com.kiwi.api.batchcloses.hexagonal.application.usecase.ProvideIdUseCase
import com.kiwi.api.batchcloses.hexagonal.domain.Authorization
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
import com.kiwi.api.batchcloses.shared.util.log.CompanionLogger
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
                createFrom(generateId(), request, it, merchantId)
            }
        }

    private fun generateId() =
        provideIdUseCase.provide()
            .log { info("id provided: {}", it) }

    private fun BatchCloseRequest.authorize() =
        authorizeUseCase.authorize(this)
            .log { info("batch close authorized: {}", it.authorizationCode) }

    private fun createFrom(id: String, request: BatchCloseRequest, authorization: Authorization, merchantId: String) =
        buildUseCase.buildFrom(request, authorization, merchantId, id)
            .log { info("batch close created: {}", it.id) }

    companion object : CompanionLogger()
}
