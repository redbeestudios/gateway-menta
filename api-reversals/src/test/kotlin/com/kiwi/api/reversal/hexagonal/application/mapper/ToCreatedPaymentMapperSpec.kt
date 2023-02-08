package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToCreatedPaymentMapperSpec : FeatureSpec({

    val idProvider = mockk<IdProvider>()
    val maskPanProvider = mockk<MaskPanProvider>()
    val mapper = ToCreatedPaymentMapper(idProvider, maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("map created payment") {

        scenario("successful mapping") {
            val payment = aPayment()
            val authorization = anAuthorization()
            val maskedCard = "XXXXXXXXXXXX4665"
            val createdPayment = aCreatedPayment().copy(
                data = payment.copy(
                    capture = payment.capture.copy(
                        card = payment.capture.card.copy(
                            pan = maskedCard
                        )
                    )
                )

            )

            every { idProvider.provide() } returns "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
            every { maskPanProvider.provide(payment.capture.card.pan!!) } returns "XXXXXXXXXXXX4665"

            mapper.map(payment, authorization) shouldBe createdPayment

            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { maskPanProvider.provide(payment.capture.card.pan!!) }
        }
    }
})
