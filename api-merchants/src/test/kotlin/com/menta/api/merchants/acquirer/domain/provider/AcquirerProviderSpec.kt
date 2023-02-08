package com.menta.api.merchants.acquirer.domain.provider

import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.acquirer.domain.Acquirer
import com.menta.api.merchants.acquirer.domain.Acquirers
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidAcquirer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class AcquirerProviderSpec : FeatureSpec({

    feature("provide acquirer") {

        scenario("acquirer found") {

            val acquirer = Acquirers(listOf(Acquirer("GPS"), Acquirer("BANORTE")))
            val provider = AcquirerProvider(acquirer)

            provider.provideFor("GPS") shouldBe Acquirer("GPS").right()
        }

        scenario("acquirer not found") {

            val acquirer = Acquirers(listOf(Acquirer("GPS"), Acquirer("BANORTE")))
            val provider = AcquirerProvider(acquirer)

            provider.provideFor("ABC") shouldBe invalidAcquirer("ABC").left()
        }
    }
})
