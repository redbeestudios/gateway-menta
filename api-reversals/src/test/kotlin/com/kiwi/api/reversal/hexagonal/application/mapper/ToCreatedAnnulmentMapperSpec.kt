package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.hexagonal.application.anAnnulment
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToCreatedAnnulmentMapperSpec : FeatureSpec({

    val idProvider = mockk<IdProvider>()
    val maskPanProvider = mockk<MaskPanProvider>()
    val mapper = ToCreatedAnnulmentMapper(idProvider, maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("map created payment") {

        scenario("successful mapping") {
            val annulment = anAnnulment()
            val authorization = anAuthorization()
            val maskedCard = "XXXXXXXXXXXX4665"
            val createdAnnulment = aCreatedAnnulment().copy(
                data = annulment.copy(
                    capture = annulment.capture.copy(
                        card = annulment.capture.card.copy(
                            pan = maskedCard
                        )
                    )
                )
            )

            every { idProvider.provide() } returns "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
            every { maskPanProvider.provide(annulment.capture.card.pan!!) } returns "XXXXXXXXXXXX4665"

            mapper.map(annulment, authorization) shouldBe createdAnnulment

            verify(exactly = 1) { idProvider.provide() }
            verify(exactly = 1) { maskPanProvider.provide(annulment.capture.card.pan!!) }
        }
    }
})
