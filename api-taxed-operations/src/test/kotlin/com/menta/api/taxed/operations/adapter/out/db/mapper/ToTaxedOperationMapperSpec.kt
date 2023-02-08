package com.menta.api.taxed.operations.adapter.out.db.mapper

import com.menta.api.taxed.operations.adapter.out.db.resolver.CreateDatetimeResolver
import com.menta.api.taxed.operations.application.aPaymentTaxedOperation
import com.menta.api.taxed.operations.application.aTaxedOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToTaxedOperationMapperSpec : FeatureSpec({

    val dateTimeResolver = CreateDatetimeResolver()
    val mapper = ToTaxedOperationMapper(dateTimeResolver)

    feature("to TaxedOperationMapper") {
        scenario("successful mapping") {
            val paymentTaxedOperation = aPaymentTaxedOperation()
            val result = aTaxedOperation()

            mapper.map(paymentTaxedOperation) shouldBe result
        }
    }
})
