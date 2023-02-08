package com.kiwi.api.batchcloses.hexagonal.application.usecase.crud

import com.kiwi.api.batchcloses.hexagonal.application.*
import com.kiwi.api.batchcloses.hexagonal.application.usecase.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateBatchCloseUseCaseSpec : FeatureSpec({

    feature("batch close creation") {

        lateinit var provideIdUseCase: ProvideIdUseCase
        lateinit var authorizeUseCase: AuthorizeBatchCloseUseCase
        lateinit var buildUseCase: BuildBatchCloseUseCase

        lateinit var useCase: CreateBatchCloseUseCase

        beforeEach {
            provideIdUseCase = mockk()
            authorizeUseCase = mockk()
            buildUseCase = mockk()

            useCase = CreateBatchCloseUseCase(
                provideIdUseCase = provideIdUseCase,
                authorizeUseCase = authorizeUseCase,
                buildUseCase = buildUseCase
            )
        }

        scenario("successful batch close creation") {
            val request = aBatchCloseRequest()
            val expectation = aBatchClose()
            val authorization = anAuthorization()
            val id = "1234"

            //given mocked dependencies
            every { provideIdUseCase.provide() } returns id
            every { authorizeUseCase.authorize(request) } returns authorization
            every { buildUseCase.buildFrom(request, authorization, merchantId, id) } returns expectation

            //expect that
            useCase.execute(request, merchantId) shouldBe expectation

            //dependencies called
            verify(exactly = 1) { provideIdUseCase.provide() }
            verify(exactly = 1) { authorizeUseCase.authorize(request) }
            verify(exactly = 1) { buildUseCase.buildFrom(request, authorization, merchantId, id) }
        }
    }
})
