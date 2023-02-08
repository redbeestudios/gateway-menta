package com.kiwi.api.reimbursements.hexagonal.domain.mapper

import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToCreatedAnnulmentMapper(
    private val maskPanProvider: MaskPanProvider
) {

    fun from(annulment: Annulment, authorization: Authorization, id: UUID) =
        CreatedAnnulment(
            id = id,
            authorization = authorization,
            data = annulment.copy(
                capture = annulment.capture.copy(
                    card = annulment.capture.card.copy(
                        pan = annulment.capture.card.pan?.mask()
                    )
                )
            )
        ).log { info("created annulment mapped: {}", it) }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
