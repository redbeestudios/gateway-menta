package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.Reimbursement
import org.springframework.stereotype.Component

@Component
class ToAcquirerRequestMapper {

    fun map(annulment: Annulment) =
        with(annulment) {
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
                        emv = AcquirerRequest.Capture.Card.EMV(
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            ksn = capture.card.ksn
                        ),
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
    fun map(refund: Refund) =
        with(refund) {
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
                        emv = AcquirerRequest.Capture.Card.EMV(
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            ksn = capture.card.ksn
                        ),
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
                        type = it.id.type,
                        number = it.id.number
                    ),
                    birthDate = it.birthDate,
                    name = it.name,
                    surname = it.surname
                )
            },
            businessOwner = businessOwner?.let {
                AcquirerRequest.Merchant.BusinessOwner(
                    birthDate = it.birthDate,
                    name = it.name,
                    surname = it.surname,
                    id = AcquirerRequest.Merchant.BusinessOwner.OwnerId(
                        type = it.id.type,
                        number = it.id.number
                    )
                )
            },
            merchantCode = merchantCode,
            address = AcquirerRequest.Merchant.Address(
                address.state,
                address.city,
                address.zip,
                address.street,
                address.number,
                address.floor,
                address.apartment
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
                settlementCondition.transactionFee,
                settlementCondition.settlement,
                settlementCondition.cbuOrCvu
            )
        )

    private fun Reimbursement.Terminal.build() =
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
}
