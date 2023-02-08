package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import org.springframework.stereotype.Component

@Component
class ToMerchantMapper {

    fun map(response: MerchantResponse): Merchant =
        with(response) {
            Merchant(
                id = id,
                customerId = customerId,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                representative = representative?.let {
                    Merchant.Representative(
                        id = Merchant.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                businessOwner = businessOwner?.let {
                    Merchant.BusinessOwner(
                        birthDate = businessOwner.birthDate,
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        id = Merchant.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                merchantCode = merchantCode,
                address = Merchant.Address(
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
                tax = Merchant.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                settlementCondition = Merchant.SettlementCondition(
                    transactionFee = settlementCondition.transactionFee,
                    settlement = settlementCondition.settlement,
                    cbuOrCvu = settlementCondition.cbuOrCvu
                )
            )
        }
}
