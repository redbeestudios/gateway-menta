package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.CustomerResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.Address
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.BusinessOwner
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.BusinessOwner.OwnerId
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.Representative
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.Representative.RepresentativeId
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.SettlementCondition
import com.kiwi.api.reimbursements.hexagonal.domain.Customer.Tax
import org.springframework.stereotype.Component

@Component
class ToCustomerMapper() {

    fun map(response: CustomerResponse): Customer =
        with(response) {
            Customer(
                id = id,
                legalType = legalType,
                country = country,
                businessName = businessName,
                fantasyName = fantasyName,
                tax = Tax(type = tax.type, id = tax.id),
                activity = activity,
                email = email,
                phone = phone,
                address = Address(
                    state = address.state,
                    city = address.city,
                    zip = address.zip,
                    street = address.street,
                    number = address.number,
                    apartment = address.apartment,
                    floor = address.floor
                ),
                businessOwner = businessOwner?.let {
                    BusinessOwner(
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        birthDate = businessOwner.birthDate,
                        ownerId = OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                representative = representative?.let {
                    Representative(
                        representativeId = RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                settlementCondition = settlementCondition?.let {
                    SettlementCondition(
                        settlementCondition.transactionFee,
                        settlementCondition.settlement,
                        settlementCondition.cbuOrCvu
                    )
                },
                status = status
            )
        }
}
