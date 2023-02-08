package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToCreatedAnnulmentMapper(
    private val idProvider: IdProvider,
    private val maskPanProvider: MaskPanProvider
) {
    fun map(annulment: Annulment, authorization: Authorization) =
        CreatedAnnulment(
            id = idProvider.provide(),
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
