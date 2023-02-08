package com.menta.api.credibanco.adapter.controller.mapper

import com.menta.api.credibanco.aCreatedOperation
import com.menta.api.credibanco.aPaymentResponse
import com.menta.api.credibanco.aRequestPayment
import com.menta.api.credibanco.adapter.controller.provider.MaskedPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOperationResponseMapperSpec : FeatureSpec({
    feature("map response") {

        beforeEach { clearAllMocks() }

        val mapper = ToOperationResponseMapper(MaskedPanProvider())

        scenario("successfully operation response mapping") {
            mapper.map(aCreatedOperation(), aRequestPayment()) shouldBe aPaymentResponse()
        }
    }
})
