package com.kiwi.api.reversal.hexagonal.adapter.out.db.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType.ANNULMENT
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID
import com.kiwi.api.reversal.hexagonal.adapter.out.db.entity.Operation as OperationEntity

@Component
class ToOperationEntityMapper(
    private val maskPanProvider: MaskPanProvider,
    private val createDatetimeResolver: CreateDatetimeResolver
) {
    fun map(createdPayment: CreatedPayment) =
        with(createdPayment) {
            OperationEntity(
                id = UUID.fromString(id),
                originalOperationId = data.operationId,
                type = OperationType.PAYMENT.name,
                acquirerId = authorization.retrievalReferenceNumber,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage,
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
                advance = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "ADVANCE" }?.amount,
                tip = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "TIP" }?.amount,
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
            )
        }

    fun map(createdRefund: CreatedRefund) =
        with(createdRefund) {
            OperationEntity(
                id = UUID.fromString(id),
                originalOperationId = data.operationId,
                type = OperationType.REFUND.name,
                acquirerId = authorization.retrievalReferenceNumber,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage,
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
                advance = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "ADVANCE" }?.amount,
                tip = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "TIP" }?.amount,
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
            )
        }

    fun map(createdAnnulment: CreatedAnnulment) =
        with(createdAnnulment) {
            OperationEntity(
                id = UUID.fromString(id),
                originalOperationId = data.operationId,
                type = ANNULMENT.name,
                acquirerId = authorization.retrievalReferenceNumber,
                authorizationCode = authorization.authorizationCode,
                displayMessage = authorization.displayMessage,
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
                advance = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "ADVANCE" }?.amount,
                tip = data.amount.breakdown.firstOrNull { breakdown -> breakdown.description == "TIP" }?.amount,
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
            )
        }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
