package com.menta.apiacquirers.domain.provider

import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class AcquirerProviderSpec : FeatureSpec({

    feature("provide acquirer by country") {

        scenario("empty acquirer list") {
            val country = "a country"

            OperableAcquirers(emptyList()).let {
                AcquirerProvider(it).provideBy(country) shouldBeLeft acquirerNotFound(country)
            }
        }

        scenario("acquirer not found for country") {
            val country = "a country"
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a different country")

            OperableAcquirers(listOf(acquirer)).let {
                AcquirerProvider(it).provideBy(country) shouldBeLeft acquirerNotFound(country)
            }
        }

        scenario("acquirer found") {
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a country")

            OperableAcquirers(listOf(acquirer)).let {
                AcquirerProvider(it).provideBy(acquirer.country) shouldBeRight acquirer
            }
        }
    }
})
