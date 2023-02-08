package com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerRequest
import com.kiwi.api.payments.hexagonal.domain.Payment
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
                trace = trace
            )
        }

    private fun Payment.Merchant.build() =
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
                        type = businessOwner.ownerId.type,
                        number = businessOwner.ownerId.number
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

    private fun Payment.Terminal.build() =
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

    private fun Payment.Customer.build() =
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
