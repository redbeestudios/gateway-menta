package com.kiwi.api.payments.hexagonal.application.mapper

import com.kiwi.api.payments.hexagonal.application.aCreatedPaymentWithMaskedPan
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.anAuthorization
import com.kiwi.api.payments.hexagonal.application.cardMaskedPan
import com.kiwi.api.payments.hexagonal.application.cardPan
import com.kiwi.api.payments.hexagonal.domain.Origin.BILL
import com.kiwi.api.payments.hexagonal.domain.provider.IdProvider
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class ToCreatedPaymentMapperSpec : FeatureSpec({
    val idProvider: IdProvider = mockk()

    feature("map created payment") {

        val maskPanProvider = mockk<MaskPanProvider>()
        val mapper = ToCreatedPaymentMapper(idProvider, maskPanProvider)

        beforeEach { clearAllMocks() }

        scenario("successful acquirer payment mapping") {
            val createdPayment = aCreatedPaymentWithMaskedPan()
            val authorization = anAuthorization()
            val payment = aPayment()

            every { maskPanProvider.provide(cardPan) } returns cardMaskedPan
            every { idProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            mapper.mapFromPayment(payment, authorization) shouldBe createdPayment

            verify(exactly = 1) { maskPanProvider.provide(cardPan) }
        }

        scenario("successful bill payment mapping") {
            val createdPayment = aCreatedPaymentWithMaskedPan().copy(origin = BILL)
            val authorization = anAuthorization()
            val payment = aPayment()

            every { maskPanProvider.provide(cardPan) } returns cardMaskedPan
            every { idProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            mapper.mapFromBillPayment(payment, authorization) shouldBe createdPayment

            verify(exactly = 1) { maskPanProvider.provide(cardPan) }
        }
    }
})
