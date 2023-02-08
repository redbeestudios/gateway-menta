package com.kiwi.api.payments.hexagonal.adapter.out.db.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.db.entity.Operation
import com.kiwi.api.payments.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.hexagonal.domain.StatusCode.REVERSAL
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOperationEntityMapper(
    private val createDatetimeResolver: CreateDatetimeResolver,
    private val maskPanProvider: MaskPanProvider
) {
    fun map(createdPayment: CreatedPayment) =
        with(createdPayment) {
            Operation(
                id = id,
                ticketId = ticketId,
                acquirerId = authorization.retrievalReferenceNumber,
                origin = origin,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage,
                statusCode = authorization.status.code.name,
                situationCode = authorization.status.situation?.id,
                situationMessage = authorization.status.situation?.description,
                merchantId = data.merchant.id,
                terminalId = data.terminal.id,
                customerId = data.customer.id,
                amount = data.amount.total,
                currency = data.amount.currency,
                trace = data.trace,
                ticket = data.ticket,
                batch = data.batch,
                advance = getAdvance(data.amount.breakdown),
                tip = getTip(data.amount.breakdown),
                installments = data.installments,
                cardHolderName = data.capture.card.holder.name,
                identityType = data.capture.card.holder.identification?.type,
                identityNumber = data.capture.card.holder.identification?.number,
                inputMode = data.capture.inputMode,
                cardBrand = data.capture.card.brand,
                cardBank = data.capture.card.bank,
                cardType = data.capture.card.type,
                cardPan = data.capture.card.pan?.mask(),
                datetime = data.datetime,
                createDatetime = createDatetimeResolver.provide()
            ).log { info("create operation mapped: {}", it) }
        }

    fun map(operation: Operation) =
        with(operation) {
            Operation(
                id = id,
                ticketId = ticketId,
                acquirerId = acquirerId,
                origin = origin,
                authorizationCode = authorizationCode,
                displayMessage = displayMessage,
                statusCode = REVERSAL.name,
                situationCode = situationCode,
                situationMessage = situationMessage,
                merchantId = merchantId,
                terminalId = terminalId,
                customerId = customerId,
                amount = amount,
                currency = currency,
                trace = trace,
                ticket = ticket,
                batch = batch,
                advance = advance,
                tip = tip,
                installments = installments,
                cardHolderName = cardHolderName,
                identityType = identityType,
                identityNumber = identityNumber,
                inputMode = inputMode,
                cardBrand = cardBrand,
                cardBank = cardBank,
                cardType = cardType,
                cardPan = cardPan,
                datetime = datetime,
                createDatetime = createDatetime
            ).log { info("create operation mapped: {}", it) }
        }

    private fun getTip(breakdowns: List<Payment.Amount.Breakdown>): String? {
        return breakdowns.firstOrNull { breakdown -> breakdown.description == "TIP" }?.amount
    }

    private fun getAdvance(breakdowns: List<Payment.Amount.Breakdown>): String? {
        return breakdowns.firstOrNull { breakdown -> breakdown.description == "ADVANCE" }?.amount
    }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
