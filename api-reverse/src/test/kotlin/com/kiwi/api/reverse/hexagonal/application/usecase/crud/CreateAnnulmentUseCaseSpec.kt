package com.kiwi.api.reverse.hexagonal.application.usecase.crud

import com.kiwi.api.reverse.hexagonal.application.anAnnulment
import com.kiwi.api.reverse.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.application.usecase.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateAnnulmentUseCaseSpec : FeatureSpec({

    feature("annulment creation") {

        lateinit var provideIdUseCase: ProvideIdUseCase
        lateinit var authorizeUseCase: AuthorizeAnnulmentUseCase
        lateinit var buildUseCase: BuildAnnulmentUseCase

        lateinit var useCase: CreateAnnulmentUseCase

        beforeEach {
            provideIdUseCase = mockk()
            authorizeUseCase = mockk()
            buildUseCase = mockk()

            useCase = CreateAnnulmentUseCase(
                provideIdUseCase = provideIdUseCase,
                authorizeUseCase = authorizeUseCase,
                buildUseCase = buildUseCase
            )
        }

        scenario("successful annulment creation") {
            val request = aReimbursementRequest()
            val expectedReimbursement = anAnnulment()
            val auth = anAuthorization()
            val id = "1234"
            val merchantId = ""


            //given mocked dependencies
            every { provideIdUseCase.provide() } returns id
            every { authorizeUseCase.authorize(request) } returns auth
            every { buildUseCase.buildFrom(request, auth, id, merchantId) } returns expectedReimbursement

            //expect that
            useCase.execute(request, merchantId) shouldBe expectedReimbursement

            //dependencies called
            verify(exactly = 1) { provideIdUseCase.provide() }
            verify(exactly = 1) { authorizeUseCase.authorize(request) }
            verify(exactly = 1) { buildUseCase.buildFrom(request, auth, id, merchantId) }
        }
    }
})
