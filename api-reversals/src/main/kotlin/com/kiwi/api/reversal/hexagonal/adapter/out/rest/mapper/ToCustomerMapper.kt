package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.CustomerResponse
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import org.springframework.stereotype.Component

@Component
class ToCustomerMapper {

    fun map(response: CustomerResponse): Customer =
        with(response) {
            Customer(
                id = id,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                tax = Customer.Tax(type = tax.type, id = tax.id),
                activity = activity,
                email = email,
                phone = phone,
                address = Customer.Address(
                    state = address.state,
                    city = address.city,
                    zip = address.zip,
                    street = address.street,
                    number = address.number,
                    apartment = address.apartment,
                    floor = address.floor
                ),
                businessOwner = businessOwner?.let {
                    Customer.BusinessOwner(
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        birthDate = businessOwner.birthDate,
                        ownerId = Customer.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                representative = representative?.let {
                    Customer.Representative(
                        representativeId = Customer.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                settlementCondition = settlementCondition?.let {
                    Customer.SettlementCondition(
                        settlementCondition.transactionFee,
                        settlementCondition.settlement,
                        settlementCondition.cbuOrCvu
                    )
                },
                status = status
            )
        }
}
