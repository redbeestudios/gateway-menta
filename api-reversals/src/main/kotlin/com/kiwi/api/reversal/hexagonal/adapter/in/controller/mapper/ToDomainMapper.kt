package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import com.kiwi.api.reversal.hexagonal.domain.entities.Terminal
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.hexagonal.domain.operations.Reimbursement
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToDomainMapper {

    fun mapToPayment(
        paymentRequest: PaymentRequest,
        merchant: Merchant,
        customer: Customer,
        terminal: ReceivedTerminal
    ): Payment =
        with(paymentRequest) {
            Payment(
                operationId = operationId,
                acquirerId = acquirerId,
                capture = Payment.Capture(
                    card = Payment.Capture.Card(
                        holder = Payment.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = capture.card.holder.identification?.let {
                                Payment.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        pan = capture.card.pan,
                        expirationDate = capture.card.expirationDate,
                        cvv = capture.card.cvv,
                        track1 = capture.card.track1,
                        track2 = capture.card.track2,
                        iccData = capture.card.iccData,
                        cardSequenceNumber = capture.card.cardSequenceNumber,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                        pin = capture.card.pin,
                        ksn = capture.card.ksn
                    ),
                    inputMode = capture.inputMode,
                    previousTransactionInputMode = capture.previousTransactionInputMode
                ),
                amount = Payment.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        Payment.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                installments = installments,
                trace = trace,
                batch = batch,
                ticket = ticket,
                terminal = build(terminal, paymentRequest.terminal.softwareVersion),
                datetime = datetime,
                merchant = merchant,
                customer = customer
            )
        }

    fun mapToAnnulment(request: ReimbursementRequest, merchant: Merchant, customer: Customer, terminal: ReceivedTerminal) =
        with(request) {
            Annulment(
                operationId = operationId,
                acquirerId = acquirerId,
                merchant = merchant,
                terminal = build(terminal, request.terminal.softwareVersion),
                capture = capture.build(),
                amount = amount.build(),
                installments = installments,
                trace = trace,
                ticket = ticket,
                batch = batch,
                datetime = datetime,
                customer = customer
            )
        }
            .log { info("annulment built: {}", it) }

    fun mapToRefund(request: ReimbursementRequest, merchant: Merchant, customer: Customer, terminal: ReceivedTerminal) =
        with(request) {
            Refund(
                operationId = operationId,
                acquirerId = acquirerId,
                merchant = merchant,
                terminal = build(terminal, request.terminal.softwareVersion),
                capture = capture.build(),
                amount = amount.build(),
                installments = installments,
                trace = trace,
                ticket = ticket,
                batch = batch,
                datetime = datetime,
                customer = customer
            )
        }
            .log { info("refund built {}", it) }

    fun mapToBatchClose(request: BatchCloseRequest, merchant: Merchant, customer: Customer, terminal: ReceivedTerminal) =
        with(request) {
            BatchClose(
                id = "id",
                authorization = BatchClose.Authorization(
                    code = "200",
                    displayMessage = "displaymessage",
                    retrievalReferenceNumber = "1456",
                    status = BatchClose.Authorization.Status(
                        code = "00",
                        situation = null
                    )
                ),
                trace = trace,
                batch = batch,
                hostMessage = "hostMessage",
                terminal = build(terminal, request.terminal.softwareVersion),
                merchant = merchant,
                ticket = ticket,
                datetime = datetime,
                totals = totals.map {
                    BatchClose.Total(
                        operationCode = it.operationCode,
                        amount = it.amount,
                        currency = it.currency
                    )
                },
                customer = customer
            )
        }.log { info("batch close response mapped: {}", it) }

    private fun ReimbursementRequest.Capture.build() =
        Reimbursement.Capture(
            card = Reimbursement.Capture.Card(
                holder = Reimbursement.Capture.Card.Holder(
                    name = card.holder.name,
                    identification = card.holder.identification?.let {
                        Reimbursement.Capture.Card.Holder.Identification(
                            number = it.number,
                            type = it.type
                        )
                    }
                ),
                pan = card.pan,
                expirationDate = card.expirationDate,
                cvv = card.cvv,
                track1 = card.track1,
                track2 = card.track2,
                iccData = card.iccData,
                cardSequenceNumber = card.cardSequenceNumber,
                bank = card.bank,
                type = card.type,
                brand = card.brand,
                pin = card.pin,
                ksn = card.ksn
            ),
            inputMode = inputMode,
            previousTransactionInputMode = previousTransactionInputMode
        )

    private fun ReimbursementRequest.Amount.build() =
        Reimbursement.Amount(
            total = total,
            currency = currency,
            breakdown = breakdown.map {
                Reimbursement.Amount.Breakdown(
                    description = it.description,
                    amount = it.amount
                )
            }
        )

    private fun build(receivedTerminal: ReceivedTerminal, softwareVersion: String) =
        Terminal(
            id = receivedTerminal.id,
            merchantId = receivedTerminal.merchantId,
            customerId = receivedTerminal.customerId,
            serialCode = receivedTerminal.serialCode,
            hardwareVersion = receivedTerminal.hardwareVersion,
            softwareVersion = softwareVersion,
            tradeMark = receivedTerminal.tradeMark,
            model = receivedTerminal.model,
            status = receivedTerminal.status,
            features = receivedTerminal.features
        )

    companion object : CompanionLogger()
}
