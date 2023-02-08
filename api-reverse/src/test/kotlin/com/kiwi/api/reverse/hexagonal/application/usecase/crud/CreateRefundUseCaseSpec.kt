package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.application.aRefund
import com.kiwi.api.reverse.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizeRefundUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildRefundUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateRefundUseCaseSpec : FeatureSpec({

    feature("refund creation") {

        lateinit var provideIdUseCase: ProvideIdUseCase
        lateinit var authorizeUseCase: AuthorizeRefundUseCase
        lateinit var buildUseCase: BuildRefundUseCase

        lateinit var useCase: CreateRefundUseCase

        beforeEach {
            provideIdUseCase = mockk()
            authorizeUseCase = mockk()
            buildUseCase = mockk()

            useCase = CreateRefundUseCase(
                provideIdUseCase = provideIdUseCase,
                authorizeUseCase = authorizeUseCase,
                buildUseCase = buildUseCase
            )
        }

        scenario("successful refund creation") {

            val request = aReimbursementRequest()
            val expectedReimbursement = aRefund()
            val auth = anAuthorization()
            val id = "1234"
            val merchantId = ""

            //given mocked dependencies
            every { provideIdUseCase.provide() } returns id
            every { authorizeUseCase.authorize(request) } returns auth
            every { buildUseCase.buildFrom(request, auth, id) } returns expectedReimbursement

            //expect that
            useCase.execute(request, merchantId) shouldBe expectedReimbursement

            //dependencies called
            verify(exactly = 1) { provideIdUseCase.provide() }
            verify(exactly = 1) { authorizeUseCase.authorize(request) }
            verify(exactly = 1) { buildUseCase.buildFrom(request, auth, id) }
        }
    }
})
