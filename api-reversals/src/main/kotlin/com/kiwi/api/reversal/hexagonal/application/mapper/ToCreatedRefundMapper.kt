package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToCreatedRefundMapper(
    private val idProvider: IdProvider,
    private val maskPanProvider: MaskPanProvider
) {

    fun map(refund: Refund, authorization: Authorization) =
        CreatedRefund(
            id = idProvider.provide(),
            authorization = authorization,
            data = refund.copy(
                capture = refund.capture.copy(
                    card = refund.capture.card.copy(
                        pan = refund.capture.card.pan?.mask()
                    )
                )
            )

        ).log { info("created refund mapped: {}", it) }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
