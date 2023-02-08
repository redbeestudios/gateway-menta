package com.menta.api.customers.customer.application.mapper

import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.PreCustomer
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.customer.domain.provider.IdProvider
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToCustomerMapper(
    private val idProvider: IdProvider,
    private val dateProvider: DateProvider
) {
    fun map(preCustomer: PreCustomer) =
        with(preCustomer) {
            Customer(
                id = idProvider.provide(),
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                activity = activity,
                email = email,
                phone = phone,
                address = Customer.Address(
                    apartment = address.apartment,
                    city = address.city,
                    floor = address.floor,
                    number = address.number,
                    state = address.state,
                    street = address.street,
                    zip = address.zip,
                ),
                tax = Customer.Tax(
                    id = tax.id,
                    type = tax.type
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
                status = Status.ACTIVE,
                deleteDate = null,
                createDate = dateProvider.provide(),
                updateDate = dateProvider.provide()
            )
        }.log { info("created annulment mapped: {}", it) }

    companion object : CompanionLogger()
}
