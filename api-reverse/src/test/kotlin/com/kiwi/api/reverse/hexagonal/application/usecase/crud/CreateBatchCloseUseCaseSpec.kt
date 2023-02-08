package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.application.aBatchClose
import com.kiwi.api.reverse.hexagonal.application.aBatchCloseRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.application.usecase.AuthorizeBatchCloseUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.BuildBatchCloseUseCase
import com.kiwi.api.reverse.hexagonal.application.usecase.ProvideIdUseCase
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
            val expectedReimbursement = aBatchClose()
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
