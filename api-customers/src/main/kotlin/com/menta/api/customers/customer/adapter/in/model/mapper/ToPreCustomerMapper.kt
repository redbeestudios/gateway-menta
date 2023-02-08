package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.customer.adapter.`in`.model.CustomerRequest
import com.menta.api.customers.customer.domain.PreCustomer
import org.springframework.stereotype.Component

@Component
class ToPreCustomerMapper {

    fun map(customerRequest: CustomerRequest): PreCustomer =
        with(customerRequest) {
            PreCustomer(
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                activity = activity,
                email = email,
                phone = phone,
                address = PreCustomer.Address(
                    apartment = address.apartment,
                    city = address.city,
                    floor = address.floor,
                    number = address.number,
                    state = address.state,
                    street = address.street,
                    zip = address.zip,
                ),
                tax = PreCustomer.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                businessOwner = businessOwner?.let {
                    PreCustomer.BusinessOwner(
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        birthDate = businessOwner.birthDate,
                        ownerId = PreCustomer.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                representative = representative?.let {
                    PreCustomer.Representative(
                        representativeId = PreCustomer.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                settlementCondition = settlementCondition?.let {
                    PreCustomer.SettlementCondition(
                        settlementCondition.transactionFee,
                        settlementCondition.settlement,
                        settlementCondition.cbuOrCvu
                    )
                }
            )
        }
}
