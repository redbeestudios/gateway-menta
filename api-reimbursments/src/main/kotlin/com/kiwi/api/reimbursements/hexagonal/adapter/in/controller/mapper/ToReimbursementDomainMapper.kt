package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.Reimbursement
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToReimbursementDomainMapper {

    fun mapToAnnulment(request: ReimbursementRequest, terminal: Terminal, merchant: Merchant, customer: Customer) =
        with(request) {
            Annulment(
                paymentId = paymentId,
                acquirerId = acquirerId,
                merchant = merchant,
                terminal = terminal.build(request),
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

    fun mapToRefund(request: ReimbursementRequest, terminal: Terminal, merchant: Merchant, customer: Customer) =
        with(request) {
            Refund(
                paymentId = paymentId,
                acquirerId = acquirerId,
                merchant = merchant,
                terminal = terminal.build(request),
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

    private fun Terminal.build(request: ReimbursementRequest) =
        Reimbursement.Terminal(
            serialCode = serialCode,
            id = id,
            softwareVersion = request.terminal.softwareVersion,
            features = features,
            merchantId = merchantId,
            customerId = customerId,
            hardwareVersion = hardwareVersion,
            model = model,
            status = status,
            tradeMark = tradeMark,
        )

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

    companion object : CompanionLogger()
}
