package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementResponse
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToResponseMapper {

    fun map(annulment: Annulment) =
        with(annulment) {
            ReimbursementResponse(
                operationId = operationId?.toString(),
                capture = ReimbursementResponse.Capture(
                    card = ReimbursementResponse.Capture.Card(
                        maskedPan = capture.card.pan,
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = capture.card.holder.identification?.let {
                                ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                        iccData = capture.card.iccData
                    ),
                    inputMode = capture.inputMode,
                    previousTransactionInputMode = capture.previousTransactionInputMode
                ),
                amount = ReimbursementResponse.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        ReimbursementResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                trace = trace,
                batch = batch,
                hostMessage = "host message",
                ticket = ticket,
                terminal = ReimbursementResponse.Terminal(
                    id = terminal.id.toString(),
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                merchant = ReimbursementResponse.Merchant(
                    id = merchant.id.toString()
                ),
                datetime = datetime,
                installments = installments,
            )
        }.log { info("annulment response mapped: {}", it) }

    fun map(refund: Refund) =
        with(refund) {
            ReimbursementResponse(
                operationId = operationId?.toString(),
                capture = ReimbursementResponse.Capture(
                    card = ReimbursementResponse.Capture.Card(
                        maskedPan = capture.card.pan,
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = capture.card.holder.identification?.let {
                                ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                        iccData = capture.card.iccData
                    ),
                    inputMode = capture.inputMode,
                    previousTransactionInputMode = capture.previousTransactionInputMode
                ),
                amount = ReimbursementResponse.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        ReimbursementResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                trace = trace,
                batch = batch,
                hostMessage = "host message",
                ticket = ticket,
                terminal = ReimbursementResponse.Terminal(
                    id = terminal.id.toString(),
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                merchant = ReimbursementResponse.Merchant(
                    id = merchant.id.toString()
                ),
                datetime = datetime,
                installments = installments,
            )
        }.log { info("refund response mapped: {}", it) }

    fun map(payment: Payment) =
        with(payment) {
            PaymentResponse(
                operationId = operationId?.toString(),
                capture = PaymentResponse.Capture(
                    card = PaymentResponse.Capture.Card(
                        holder = PaymentResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = capture.card.holder.identification?.let {
                                PaymentResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        maskedPan = capture.card.pan,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                        iccData = capture.card.iccData
                    ),
                    inputMode = capture.inputMode,
                    previousTransactionInputMode = capture.previousTransactionInputMode
                ),
                amount = PaymentResponse.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        PaymentResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                installments = installments,
                trace = trace,
                batch = batch,
                hostMessage = "host message",
                ticket = ticket,
                terminal = PaymentResponse.Terminal(
                    id = terminal.id.toString(),
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                merchant = PaymentResponse.Merchant(
                    id = merchant.id.toString()
                ),
                datetime = datetime
            )
        }.log { info("payment response mapped: {}", it) }

    fun map(batchClose: BatchClose) =
        with(batchClose) {
            BatchCloseResponse(
                id = id,
                status = BatchCloseResponse.Status(
                    code = authorization.status.code,
                    situation = authorization.status.situation?.let {
                        BatchCloseResponse.Status.Situation(
                            id = it.id,
                            description = it.description
                        )
                    }
                ),
                authorization = BatchCloseResponse.Authorization(
                    code = authorization.code,
                    displayMessage = authorization.displayMessage,
                    retrievalReferenceNumber = authorization.retrievalReferenceNumber
                ),
                trace = trace,
                batch = batch,
                hostMessage = hostMessage,
                terminal = BatchCloseResponse.Terminal(
                    id = terminal.id.toString(),
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                merchant = BatchCloseResponse.Merchant(
                    id = merchant.id.toString()
                ),
                ticket = ticket,
                datetime = datetime,
                totals = totals.map {
                    BatchCloseResponse.Total(
                        operationCode = it.operationCode,
                        amount = it.amount,
                        currency = it.currency
                    )
                }
            )
        }.log { info("batch close response mapped: {}", it) }

    companion object : CompanionLogger()
}
