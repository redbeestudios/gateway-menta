package com.kiwi.api.reimbursements.hexagonal.domain.mapper

import com.kiwi.api.reimbursements.hexagonal.application.aRefund
import com.kiwi.api.reimbursements.hexagonal.application.anAuthorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class ToCreatedRefundMapperSpec : FeatureSpec({

    val maskPanProvider = mockk<MaskPanProvider>()
    val mapper = ToCreatedRefundMapper(maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("from refund, auth and id") {

        scenario("map") {
            val auth = anAuthorization()
            val refund = aRefund()
            val id = UUID.randomUUID()
            val maskedPan = "a masked pan"
            val maskedRefund = refund.copy(
                capture = refund.capture.copy(
                    card = refund.capture.card.copy(
                        pan = maskedPan
                    )
                )
            )

            every { maskPanProvider.provide(refund.capture.card.pan!!) } returns maskedPan

            mapper.from(refund, auth, id) shouldBe CreatedRefund(
                id = id,
                authorization = auth,
                data = maskedRefund
            )

            verify(exactly = 1) { maskPanProvider.provide(refund.capture.card.pan!!) }
        }
    }
})
