package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToCreatedRefundMapperSpec : FeatureSpec({

    val idProvider = mockk<IdProvider>()
    val maskPanProvider = mockk<MaskPanProvider>()
    val mapper = ToCreatedRefundMapper(idProvider, maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("map created refund") {

        scenario("successful mapping") {
            val refund = aRefund()
            val authorization = anAuthorization()
            val maskedCard = "XXXXXXXXXXXX4665"
            val createdRefund = aCreatedRefund().copy(
                data = refund.copy(
                    capture = refund.capture.copy(
                        card = refund.capture.card.copy(
                            pan = maskedCard
                        )
                    )
                )
            )

            every { idProvider.provide() } returns "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
            every { maskPanProvider.provide(refund.capture.card.pan!!) } returns "XXXXXXXXXXXX4665"

            mapper.map(refund, authorization) shouldBe createdRefund

            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { maskPanProvider.provide(refund.capture.card.pan!!) }
        }
    }
})
