package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import com.kiwi.api.reversal.hexagonal.domain.entities.Terminal
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.hexagonal.domain.operations.Reimbursement
import org.springframework.stereotype.Component

@Component
class ToAcquirerRequestMapper {

    fun map(payment: Payment) =
        with(payment) {
            AcquirerRequest(
                capture = AcquirerRequest.Capture(
                    card = AcquirerRequest.Capture.Card(
                        holder = AcquirerRequest.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = capture.card.holder.identification?.let {
                                AcquirerRequest.Capture.Card.Holder.Identification(
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
                        pin = capture.card.pin,
                        emv = capture.card.getEmv(),
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                    ),
                    inputMode = capture.inputMode.name,
                    previousTransactionInputMode = capture.previousTransactionInputMode
                ),
                amount = AcquirerRequest.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        AcquirerRequest.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                batch = batch,
                datetime = datetime,
                installments = installments,
                merchant = merchant.build(),
                terminal = terminal.build(),
                customer = customer.build(),
                ticket = ticket,
                trace = trace,
                retrievalReferenceNumber = acquirerId
            )
        }

    fun map(annulment: Annulment) =
        with(annulment) {
            AcquirerRequest(
                retrievalReferenceNumber = acquirerId,
                capture = capture.build(),
                amount = amount.build(),
                batch = batch,
                datetime = datetime,
                installments = installments,
                merchant = merchant.build(),
                terminal = terminal.build(),
                customer = customer.build(),
                ticket = ticket,
                trace = trace
            )
        }

    fun map(refund: Refund) =
        with(refund) {
            AcquirerRequest(
                retrievalReferenceNumber = acquirerId,
                capture = capture.build(),
                amount = amount.build(),
                batch = batch,
                datetime = datetime,
                installments = installments,
                merchant = merchant.build(),
                terminal = terminal.build(),
                customer = customer.build(),
                ticket = ticket,
                trace = trace
            )
        }

    private fun Reimbursement.Capture.build() =
        AcquirerRequest.Capture(
            card = AcquirerRequest.Capture.Card(
                holder = AcquirerRequest.Capture.Card.Holder(
                    name = card.holder.name,
                    identification = card.holder.identification?.let {
                        AcquirerRequest.Capture.Card.Holder.Identification(
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
                pin = card.pin,
                emv = card.getEnv(),
                bank = card.bank,
                type = card.type,
                brand = card.brand,
            ),
            inputMode = inputMode.name,
            previousTransactionInputMode = previousTransactionInputMode
        )

    private fun Reimbursement.Amount.build() =
        AcquirerRequest.Amount(
            total = total,
            currency = currency,
            breakdown = breakdown.map {
                AcquirerRequest.Amount.Breakdown(
                    description = it.description,
                    amount = it.amount
                )
            }
        )

    private fun Merchant.build() =
        AcquirerRequest.Merchant(
            id = id,
            customerId = customerId,
            country = country,
            legalType = legalType,
            businessName = businessName,
            fantasyName = fantasyName,
            representative = representative?.let {
                AcquirerRequest.Merchant.Representative(
                    id = AcquirerRequest.Merchant.Representative.RepresentativeId(
                        type = representative.id.type,
                        number = representative.id.number
                    ),
                    birthDate = representative.birthDate,
                    name = representative.name,
                    surname = representative.surname
                )
            },
            businessOwner = businessOwner?.let {
                AcquirerRequest.Merchant.BusinessOwner(
                    birthDate = businessOwner.birthDate,
                    name = businessOwner.name,
                    surname = businessOwner.surname,
                    id = AcquirerRequest.Merchant.BusinessOwner.OwnerId(
                        type = businessOwner.id.type,
                        number = businessOwner.id.number
                    )
                )
            },
            merchantCode = merchantCode,
            address = AcquirerRequest.Merchant.Address(
                state = address.state,
                city = address.city,
                zip = address.zip,
                street = address.street,
                number = address.number,
                floor = address.floor,
                apartment = address.apartment
            ),
            email = email,
            phone = phone,
            activity = activity,
            category = category,
            tax = AcquirerRequest.Merchant.Tax(
                id = tax.id,
                type = tax.type
            ),
            settlementCondition = AcquirerRequest.Merchant.SettlementCondition(
                transactionFee = settlementCondition.transactionFee,
                settlement = settlementCondition.settlement,
                cbuOrCvu = settlementCondition.cbuOrCvu
            )
        )

    private fun Terminal.build() =
        AcquirerRequest.Terminal(
            id = id,
            merchantId = merchantId,
            customerId = customerId,
            serialCode = serialCode,
            hardwareVersion = hardwareVersion,
            softwareVersion = softwareVersion,
            tradeMark = tradeMark,
            model = model,
            status = status,
            features = features
        )

    private fun Customer.build() =
        AcquirerRequest.Customer(
            id = id,
            country = country,
            legalType = legalType,
            businessName = businessName,
            fantasyName = fantasyName,
            tax = AcquirerRequest.Customer.Tax(type = tax.type, id = tax.id),
            activity = activity,
            email = email,
            phone = phone,
            address = AcquirerRequest.Customer.Address(
                state = address.state,
                city = address.city,
                zip = address.zip,
                street = address.street,
                number = address.number,
                floor = address.floor,
                apartment = address.apartment
            ),
            businessOwner = businessOwner?.let {
                AcquirerRequest.Customer.BusinessOwner(
                    name = businessOwner.name,
                    surname = businessOwner.surname,
                    birthDate = businessOwner.birthDate,
                    ownerId = AcquirerRequest.Customer.BusinessOwner.OwnerId(
                        type = businessOwner.ownerId.type,
                        number = businessOwner.ownerId.number
                    )
                )
            },
            representative = representative?.let {
                AcquirerRequest.Customer.Representative(
                    representativeId = AcquirerRequest.Customer.Representative.RepresentativeId(
                        type = representative.representativeId.type,
                        number = representative.representativeId.number
                    ),
                    birthDate = representative.birthDate,
                    name = representative.name,
                    surname = representative.surname
                )
            },
            settlementCondition = settlementCondition?.let {
                AcquirerRequest.Customer.SettlementCondition(
                    settlementCondition.transactionFee,
                    settlementCondition.settlement,
                    settlementCondition.cbuOrCvu
                )
            },
            status = status
        )

    private fun Payment.Capture.Card.getEmv() =
        getEnv(iccData, cardSequenceNumber, ksn)

    private fun Reimbursement.Capture.Card.getEnv() =
        getEnv(iccData, cardSequenceNumber, ksn)

    private fun getEnv(iccData: String?, cardSequenceNumber: String?, ksn: String?) =
        if (iccData != null && cardSequenceNumber != null && ksn != null) {
            AcquirerRequest.Capture.Card.EMV(
                iccData = iccData,
                cardSequenceNumber = cardSequenceNumber,
                ksn = ksn
            )
        } else null
}
