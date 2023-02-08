package com.kiwi.api.payments.adapter.jpos.provider

import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.APP_VERSION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TICKET_INFORMATION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.VARIOUS_INDICATORS
import com.kiwi.api.payments.application.aDirectMerchantOperation
import com.kiwi.api.payments.application.anOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class IsoFieldValueProviderSpec : FeatureSpec({

    feature("provide requests") {

        val provider = IsoFieldValueProvider()

        scenario("Aggregator Merchant") {
            forAll(
                row(VARIOUS_INDICATORS, "021000100107070680008004097500000209812020166MENTA  *MENTA SUBCOM020167Miguel Calixto D 3120041687372003169666013173CORDOBA CAPIT00217404"),
                row(APP_VERSION, "1.0.456duro200"),
                row(TICKET_INFORMATION, "023412234566")
            ) { position, result ->
                provider.provide(position, anOperation).shouldBe(result)
            }
        }

        scenario("Direct Merchant") {
            forAll(
                row(VARIOUS_INDICATORS, "02100010010707"),
                row(APP_VERSION, "1.0.456duro200"),
                row(TICKET_INFORMATION, "023412234566")
            ) { position, result ->
                provider.provide(position, aDirectMerchantOperation).shouldBe(result)
            }
        }
    }
})
