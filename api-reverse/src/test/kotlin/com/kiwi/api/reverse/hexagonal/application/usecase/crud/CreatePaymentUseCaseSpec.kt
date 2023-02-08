package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.application.aPayment
import com.kiwi.api.reverse.hexagonal.application.aPaymentRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizePaymentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildPaymentUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreatePaymentUseCaseSpec : FeatureSpec({

    feature("payment creation") {

        lateinit var provideIdUseCase: ProvideIdUseCase
        lateinit var authorizeUseCase: AuthorizePaymentUseCase
        lateinit var buildUseCase: BuildPaymentUseCase

        lateinit var useCase: CreatePaymentUseCase

        beforeEach {
            provideIdUseCase = mockk()
            authorizeUseCase = mockk()
            buildUseCase = mockk()

            useCase = CreatePaymentUseCase(
                provideIdUseCase = provideIdUseCase,
                authorizeUseCase = authorizeUseCase,
                buildUseCase = buildUseCase
            )
        }

        scenario("successful payment creation") {
            val request = aPaymentRequest()
            val expectedReimbursement = aPayment()
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
