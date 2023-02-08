package com.kiwi.api.payments.hexagonal.application.mapper

import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Origin
import com.kiwi.api.payments.hexagonal.domain.Origin.ACQUIRER
import com.kiwi.api.payments.hexagonal.domain.Origin.BILL
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.hexagonal.domain.provider.IdProvider
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import kotlin.math.absoluteValue

@Component
class ToCreatedPaymentMapper(
    private val idProvider: IdProvider,
    private val maskPanProvider: MaskPanProvider
) {
    fun mapFromPayment(payment: Payment, authorization: Authorization) =
        map(payment, authorization, ACQUIRER)

    fun mapFromBillPayment(payment: Payment, authorization: Authorization) =
        map(payment, authorization, BILL)

    private fun map(payment: Payment, authorization: Authorization, origin: Origin) =
        with(getId()) {
            CreatedPayment(
                id = this,
                ticketId = this.hashCode().absoluteValue,
                origin = origin,
                authorization = authorization,
                data = payment.copy(
                    capture = payment.capture.copy(
                        card = payment.capture.card.copy(
                            pan = payment.capture.card.pan?.mask()
                        )
                    )
                )
            )
        }
            .log { info("created payment mapped: {}", it) }

    private fun getId() = idProvider.provide()

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
