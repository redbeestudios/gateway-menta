package com.kiwi.api.reverse.hexagonal.application.usecase

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.string.shouldMatch

class ProvideIdUseCasSpec : FeatureSpec({

    feature("provide id for payments") {

        val provider = ProvidePaymentIdUseCaseImpls().provideIdUseCase()

        scenario("id provided") {

            provider.provide() shouldMatch "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
        }
    }

    feature("provide id for reimbursements") {

        val provider = ProvideReimbursementIdUseCaseImpls().provideIdUseCase()

        scenario("id provided") {

            provider.provide() shouldMatch "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
        }
    }

    feature("provide id for batch closes") {

        val provider = ProvideBatchCloseIdUseCaseImpls().provideIdUseCase()

        scenario("id provided") {

            provider.provide() shouldMatch "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
        }
    }
})
