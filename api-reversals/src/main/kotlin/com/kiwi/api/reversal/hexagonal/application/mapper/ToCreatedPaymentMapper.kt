package com.kiwi.api.reversal.hexagonal.application.mapper

import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToCreatedPaymentMapper(
    private val idProvider: IdProvider,
    private val maskPanProvider: MaskPanProvider
) {
    fun map(payment: Payment, authorization: Authorization) =
        CreatedPayment(
            id = idProvider.provide(),
            authorization = authorization,
            data = payment.copy(
                capture = payment.capture.copy(
                    card = payment.capture.card.copy(
                        pan = payment.capture.card.pan?.mask()
                    )
                )
            )
        ).log { info("created payment mapped: {}", it) }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }
    companion object : CompanionLogger()
}
