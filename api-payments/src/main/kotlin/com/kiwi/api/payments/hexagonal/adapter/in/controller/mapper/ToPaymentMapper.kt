package com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.hexagonal.domain.Terminal
import org.springframework.stereotype.Component

@Component
class ToPaymentMapper {

    fun map(
        paymentRequest: PaymentRequest,
        merchant: Payment.Merchant,
        customer: Payment.Customer,
        terminal: Terminal,
    ): Payment =
        with(paymentRequest) {
            Payment(
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
                terminal = Payment.Terminal(
                    id = terminal.id,
                    merchantId = terminal.merchantId,
                    customerId = terminal.customerId,
                    serialCode = terminal.serialCode,
                    hardwareVersion = terminal.hardwareVersion,
                    softwareVersion = paymentRequest.terminal.softwareVersion,
                    tradeMark = terminal.tradeMark,
                    model = terminal.model,
                    status = terminal.status,
                    features = terminal.features
                ),
                datetime = datetime,
                merchant = Payment.Merchant(
                    id = merchant.id,
                    customerId = merchant.customerId,
                    country = merchant.country,
                    legalType = merchant.legalType,
                    businessName = merchant.businessName,
                    fantasyName = merchant.fantasyName,
                    representative = merchant.representative?.let {
                        Payment.Merchant.Representative(
                            id = Payment.Merchant.Representative.RepresentativeId(
                                type = merchant.representative.id.type,
                                number = merchant.representative.id.number
                            ),
                            birthDate = merchant.representative.birthDate,
                            name = merchant.representative.name,
                            surname = merchant.representative.surname
                        )
                    },
                    businessOwner = merchant.businessOwner?.let {
                        Payment.Merchant.BusinessOwner(
                            birthDate = merchant.businessOwner.birthDate,
                            name = merchant.businessOwner.name,
                            surname = merchant.businessOwner.surname,
                            ownerId = Payment.Merchant.BusinessOwner.OwnerId(
                                type = merchant.businessOwner.ownerId.type,
                                number = merchant.businessOwner.ownerId.number
                            )
                        )
                    },
                    merchantCode = merchant.merchantCode,
                    address = Payment.Merchant.Address(
                        merchant.address.state,
                        merchant.address.city,
                        merchant.address.zip,
                        merchant.address.street,
                        merchant.address.number,
                        merchant.address.floor,
                        merchant.address.apartment
                    ),
                    email = merchant.email,
                    phone = merchant.phone,
                    activity = merchant.activity,
                    category = merchant.category,
                    tax = Payment.Merchant.Tax(
                        id = merchant.tax.id,
                        type = merchant.tax.type
                    ),
                    settlementCondition = Payment.Merchant.SettlementCondition(
                        merchant.settlementCondition.transactionFee,
                        merchant.settlementCondition.settlement,
                        merchant.settlementCondition.cbuOrCvu
                    )
                ),
                customer = Payment.Customer(
                    id = customer.id,
                    country = customer.country,
                    legalType = customer.legalType,
                    businessName = customer.businessName,
                    fantasyName = customer.fantasyName,
                    tax = Payment.Customer.Tax(type = customer.tax.type, id = customer.tax.id),
                    activity = customer.activity,
                    email = customer.email,
                    phone = customer.phone,
                    address = Payment.Customer.Address(
                        state = customer.address.state,
                        city = customer.address.city,
                        zip = customer.address.zip,
                        street = customer.address.street,
                        number = customer.address.number,
                        floor = customer.address.floor,
                        apartment = customer.address.apartment
                    ),
                    businessOwner = customer.businessOwner?.let {
                        Payment.Customer.BusinessOwner(
                            name = customer.businessOwner.name,
                            surname = customer.businessOwner.surname,
                            birthDate = customer.businessOwner.birthDate,
                            ownerId = Payment.Customer.BusinessOwner.OwnerId(
                                type = customer.businessOwner.ownerId.type,
                                number = customer.businessOwner.ownerId.number
                            )
                        )
                    },
                    representative = customer.representative?.let {
                        Payment.Customer.Representative(
                            representativeId = Payment.Customer.Representative.RepresentativeId(
                                type = customer.representative.representativeId.type,
                                number = customer.representative.representativeId.number
                            ),
                            birthDate = customer.representative.birthDate,
                            name = customer.representative.name,
                            surname = customer.representative.surname
                        )
                    },
                    settlementCondition = customer.settlementCondition?.let {
                        Payment.Customer.SettlementCondition(
                            customer.settlementCondition.transactionFee,
                            customer.settlementCondition.settlement,
                            customer.settlementCondition.cbuOrCvu
                        )
                    },
                    status = customer.status
                )
            )
        }
}
