package com.kiwi.api.reimbursements.hexagonal.domain.mapper

import com.kiwi.api.reimbursements.hexagonal.application.anAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.anAuthorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class ToCreatedAnnulmentMapperSpec : FeatureSpec({

    val maskPanProvider = mockk<MaskPanProvider>()
    val mapper = ToCreatedAnnulmentMapper(maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("from annulment, auth and id") {

        scenario("map") {
            val auth = anAuthorization()
            val annulment = anAnnulment()
            val id = UUID.randomUUID()
            val maskedPan = "a masked pan"
            val maskedAnnulment = annulment.copy(
                capture = annulment.capture.copy(
                    card = annulment.capture.card.copy(
                        pan = maskedPan
                    )
                )
            )

            every { maskPanProvider.provide(annulment.capture.card.pan!!) } returns maskedPan

            mapper.from(annulment, auth, id) shouldBe CreatedAnnulment(
                id = id,
                authorization = auth,
                data = maskedAnnulment
            )

            verify(exactly = 1) { maskPanProvider.provide(annulment.capture.card.pan!!) }
        }
    }
})
