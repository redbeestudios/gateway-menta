package com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.CustomerResponse
import com.kiwi.api.payments.hexagonal.domain.Payment
import org.springframework.stereotype.Component

@Component
class ToCustomerMapper() {

    fun map(response: CustomerResponse): Payment.Customer =
        with(response) {
            Payment.Customer(
                id = id,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                tax = Payment.Customer.Tax(type = tax.type, id = tax.id),
                activity = activity,
                email = email,
                phone = phone,
                address = Payment.Customer.Address(
                    state = address.state,
                    city = address.city,
                    zip = address.zip,
                    street = address.street,
                    number = address.number,
                    apartment = address.apartment,
                    floor = address.floor
                ),
                businessOwner = businessOwner?.let {
                    Payment.Customer.BusinessOwner(
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        birthDate = businessOwner.birthDate,
                        ownerId = Payment.Customer.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                representative = representative?.let {
                    Payment.Customer.Representative(
                        representativeId = Payment.Customer.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                settlementCondition = settlementCondition?.let {
                    Payment.Customer.SettlementCondition(
                        settlementCondition.transactionFee,
                        settlementCondition.settlement,
                        settlementCondition.cbuOrCvu
                    )
                },
                status = status
            )
        }
}
