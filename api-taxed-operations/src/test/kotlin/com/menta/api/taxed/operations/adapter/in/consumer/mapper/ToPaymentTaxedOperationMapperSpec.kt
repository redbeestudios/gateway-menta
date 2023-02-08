package com.menta.api.taxed.operations.adapter.`in`.consumer.mapper

import com.menta.api.taxed.operations.application.aPaymentTaxCalculation
import com.menta.api.taxed.operations.application.aPaymentTaxedOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPaymentTaxedOperationMapperSpec : FeatureSpec({
    val mapper = ToPaymentTaxedOperationMapper()

    feature("to payment taxed operation mapper") {
        val paymentTaxCalculation = aPaymentTaxCalculation()
        val paymentTaxedOperation = aPaymentTaxedOperation()
        scenario("mapping successfull") {

            mapper.map(paymentTaxCalculation) shouldBe paymentTaxedOperation
        }
    }
})
