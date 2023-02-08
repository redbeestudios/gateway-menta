package com.kiwi.api.reimbursements.hexagonal.domain.mapper

import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToCreatedRefundMapper(
    private val maskPanProvider: MaskPanProvider
) {

    fun from(refund: Refund, authorization: Authorization, id: UUID) =
        CreatedRefund(
            id = id,
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
