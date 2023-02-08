package com.kiwi.api.reimbursements.hexagonal.adapter.out.db.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.entity.Operation
import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.OperationType.ANNULMENT
import com.kiwi.api.reimbursements.hexagonal.domain.OperationType.REFUND
import com.kiwi.api.reimbursements.hexagonal.domain.Reimbursement
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOperationMapper(
    private val maskPanProvider: MaskPanProvider,
    private val createDatetimeResolver: CreateDatetimeResolver
) {
    fun map(createdAnnulment: CreatedAnnulment) =
        with(createdAnnulment) {
            Operation(
                id = id,
                type = ANNULMENT.name,
                paymentId = data.paymentId,
                acquirerId = authorization.retrievalReferenceNumber,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage?.message,
                statusCode = authorization.status.code,
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
                operationDatetime = data.datetime,
                createDatetime = createDatetimeResolver.provide()
            ).log { info("create operation annulment mapped: {}", it) }
        }

    fun map(createdRefund: CreatedRefund) =
        with(createdRefund) {
            Operation(
                id = id,
                type = REFUND.name,
                paymentId = data.paymentId,
                acquirerId = authorization.retrievalReferenceNumber,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage?.message,
                statusCode = authorization.status.code,
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
                operationDatetime = data.datetime,
                createDatetime = createDatetimeResolver.provide()
            ).log { info("create operation refund mapped: {}", it) }
        }

    private fun getTip(breakdowns: List<Reimbursement.Amount.Breakdown>): String? {
        return breakdowns.firstOrNull { breakdown -> breakdown.description == "TIP" }?.amount
    }

    private fun getAdvance(breakdowns: List<Reimbursement.Amount.Breakdown>): String? {
        return breakdowns.firstOrNull { breakdown -> breakdown.description == "ADVANCE" }?.amount
    }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
