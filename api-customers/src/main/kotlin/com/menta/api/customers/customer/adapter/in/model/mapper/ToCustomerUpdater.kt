package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.customer.adapter.`in`.model.UpdateRequest
import com.menta.api.customers.customer.domain.Customer
import org.springframework.stereotype.Component

@Component
class ToCustomerUpdater {

    fun applyChanges(customer: Customer, changes: UpdateRequest) =
        with(customer) {
            copy(
                country = changes.country ?: country,
                legalType = changes.legalType ?: legalType,
                businessName = changes.businessName ?: businessName,
                fantasyName = changes.fantasyName ?: fantasyName,
                tax = tax.copy( // TODO: validar??
                    type = changes.tax?.type ?: tax.type,
                    id = changes.tax?.id ?: tax.id
                ),
                activity = changes.activity ?: activity,
                address = address.copy(
                    state = changes.address?.state ?: address.state,
                    city = changes.address?.city ?: address.city,
                    zip = changes.address?.zip ?: address.zip,
                    street = changes.address?.street ?: address.street,
                    number = changes.address?.number ?: address.number,
                    floor = changes.address?.floor ?: address.floor,
                    apartment = changes.address?.apartment ?: address.apartment
                ),
                email = changes.email ?: email,
                phone = changes.phone ?: phone,
                representative = representative?.copy(
                    representativeId = representative.representativeId.copy(
                        type = changes.representative?.representativeId?.type ?: representative.representativeId.type,
                        number = changes.representative?.representativeId?.number
                            ?: representative.representativeId.number
                    ),
                    birthDate = changes.representative?.birthDate ?: representative.birthDate,
                    name = changes.representative?.name ?: representative.name,
                    surname = changes.representative?.surname ?: representative.surname
                ),
                businessOwner = businessOwner?.copy(
                    name = changes.businessOwner?.name ?: businessOwner.name,
                    surname = changes.businessOwner?.surname ?: businessOwner.surname,
                    birthDate = changes.businessOwner?.birthDate ?: businessOwner.birthDate,
                    ownerId = businessOwner.ownerId.copy(
                        type = changes.businessOwner?.ownerId?.type ?: businessOwner.ownerId.type,
                        number = changes.businessOwner?.ownerId?.number ?: businessOwner.ownerId.number
                    )
                ),
                settlementCondition = settlementCondition?.copy(
                    transactionFee = changes.settlementCondition?.transactionFee ?: settlementCondition.transactionFee,
                    settlement = changes.settlementCondition?.settlement ?: settlementCondition.settlement,
                    cbuOrCvu = changes.settlementCondition?.cbuOrCvu ?: settlementCondition.cbuOrCvu
                )
            )
        }
}
