package com.menta.api.credibanco.adapter.jpos.provider

import com.menta.api.credibanco.adapter.jpos.models.FieldPosition
import com.menta.api.credibanco.anOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class IsoFieldValueProviderSpec : FeatureSpec({

    feature("provide requests") {

        val provider = IsoFieldValueProvider()

        scenario("credibanco request") {
            forAll(
                row(FieldPosition.MTI, "0200"),
                row(FieldPosition.PROCESS_CODE, "000030"),
                row(FieldPosition.TRANSMISSION_DATE_TIME, "0119112323"),
                row(FieldPosition.TERMINAL_LOCAL_TIME, "112323"),
                row(FieldPosition.TERMINAL_LOCAL_DATE, "0119"),
                row(FieldPosition.TERMINAL_CAPTURE_DATE, "0119"),
                row(FieldPosition.TRACK_2, "5413330089020011=2512601079360805"),
                row(FieldPosition.COMMERCE_NUMBER, "01  010029213"),
                row(FieldPosition.COMMERCE_NAME_LOCATION, "Gerbers gin           5001Medellin 50 CO")
            ) { position, result ->
                provider.provide(position, anOperation).shouldBe(result)
            }
        }
    }
})
