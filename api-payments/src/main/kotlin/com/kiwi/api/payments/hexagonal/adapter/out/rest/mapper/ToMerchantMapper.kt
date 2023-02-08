package com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.MerchantResponse
import com.kiwi.api.payments.hexagonal.domain.Payment
import org.springframework.stereotype.Component

@Component
class ToMerchantMapper {

    fun map(response: MerchantResponse): Payment.Merchant =
        with(response) {
            Payment.Merchant(
                id = id,
                customerId = customerId,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                representative = representative?.let {
                    Payment.Merchant.Representative(
                        id = Payment.Merchant.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                businessOwner = businessOwner?.let {
                    Payment.Merchant.BusinessOwner(
                        birthDate = businessOwner.birthDate,
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        ownerId = Payment.Merchant.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                merchantCode = merchantCode,
                address = Payment.Merchant.Address(
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
                tax = Payment.Merchant.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                settlementCondition = Payment.Merchant.SettlementCondition(
                    transactionFee = settlementCondition.transactionFee,
                    settlement = settlementCondition.settlement,
                    cbuOrCvu = settlementCondition.cbuOrCvu
                )
            )
        }
}
