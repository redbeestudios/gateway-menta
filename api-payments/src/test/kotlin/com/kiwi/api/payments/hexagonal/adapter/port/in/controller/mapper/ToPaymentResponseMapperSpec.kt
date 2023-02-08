package com.kiwi.api.payments.hexagonal.adapter.port.`in`.controller.mapper

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentResponseMapper
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.aPaymentResponse
import com.kiwi.api.payments.hexagonal.application.cardMaskedPan
import com.kiwi.api.payments.hexagonal.application.cardPan
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToPaymentResponseMapperSpec : FeatureSpec({

    feature("map response") {

        val maskPanProvider = mockk<MaskPanProvider>()
        val mapper = ToPaymentResponseMapper(maskPanProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val createdPayment = aCreatedPayment()
            val paymentResponse = aPaymentResponse()

            every { maskPanProvider.provide(cardPan) } returns cardMaskedPan

            mapper.map(createdPayment) shouldBe paymentResponse

            verify(exactly = 1) { maskPanProvider.provide(cardPan) }
        }
    }
})
