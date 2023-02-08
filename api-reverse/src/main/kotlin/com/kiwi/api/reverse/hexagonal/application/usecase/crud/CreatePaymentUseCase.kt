package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.PaymentRequest
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizePaymentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildPaymentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.hexagonal.domain.Payment
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CreatePaymentUseCase(
    @Qualifier("providePaymentIdUseCase")
    private val provideIdUseCase: ProvideIdUseCase,
    private val authorizeUseCase: AuthorizePaymentUseCase,
    private val buildUseCase: BuildPaymentUseCase
) : CreatePaymentPortIn {

    override fun execute(paymentRequest: PaymentRequest, merchantId: String): Payment =
        with(paymentRequest) {
            authorize().let {
                createFrom(generateId(), paymentRequest, it)
            }
        }

    private fun generateId() =
        provideIdUseCase.provide()
            .log { info("id provided: {}", it) }

    private fun PaymentRequest.authorize() =
        authorizeUseCase.authorize(this)
            .log { info("payment authorized: {}", it.authorizationCode) }

    private fun createFrom(id: String, paymentRequest: PaymentRequest, authorization: Authorization) =
        buildUseCase.buildFrom(paymentRequest, authorization, id)
            .log { info("payment created: {}", it.id) }

    companion object : CompanionLogger()
}
