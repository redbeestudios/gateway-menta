package com.menta.api.merchants.adapter.`in`.model.mapper

import com.menta.api.merchants.adapter.`in`.model.MerchantRequest
import com.menta.api.merchants.adapter.`in`.model.UpdateRequest
import com.menta.api.merchants.domain.Merchant
import org.springframework.stereotype.Component

@Component
class ToMerchantUpdater {

    fun applyChanges(merchant: Merchant, changes: UpdateRequest) =
        with(merchant) {
            copy(
                customerId = changes.customerId ?: customerId,
                country = changes.country ?: country,
                legalType = changes.legalType ?: legalType,
                businessName = changes.businessName ?: businessName,
                fantasyName = changes.fantasyName ?: fantasyName,
                representative = representative?.copy(
                    representativeId = representative.representativeId.copy(
                        type = changes.representative?.representativeId?.type ?: representative.representativeId.type,
                        number = changes.representative?.representativeId?.number
                            ?: representative.representativeId.number,
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
                        number = changes.businessOwner?.ownerId?.number ?: businessOwner.ownerId.number,
                    )
                ),
                merchantCode = changes.merchantCode ?: merchantCode,
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
                activity = changes.activity ?: activity,
                category = changes.category ?: category,
                tax = tax.copy(
                    id = changes.tax?.id ?: tax.id,
                    type = changes.tax?.type ?: tax.type
                ),
                settlementCondition = settlementCondition.copy(
                    transactionFee = changes.settlementCondition?.transactionFee ?: settlementCondition.transactionFee,
                    settlement = changes.settlementCondition?.settlement ?: settlementCondition.settlement,
                    cbuOrCvu = changes.settlementCondition?.cbuOrCvu ?: settlementCondition.cbuOrCvu,
                )
            )
        }

    fun applyChanges(merchant: Merchant, changes: MerchantRequest) =
        with(merchant) {
            copy(
                customerId = changes.customerId,
                country = changes.country,
                legalType = changes.legalType,
                businessName = changes.businessName,
                fantasyName = changes.fantasyName,
                representative = changes.representative?.let {
                    Merchant.Representative(
                        representativeId = Merchant.Representative.RepresentativeId(
                            type = it.representativeId.type,
                            number = it.representativeId.number
                        ),
                        birthDate = it.birthDate,
                        name = it.name,
                        surname = it.surname
                    )
                },
                businessOwner = changes.businessOwner?.let {
                    Merchant.BusinessOwner(
                        name = it.name,
                        surname = it.surname,
                        birthDate = it.birthDate,
                        ownerId = Merchant.BusinessOwner.OwnerId(
                            type = it.ownerId.type,
                            number = it.ownerId.number
                        )
                    )
                },
                merchantCode = changes.merchantCode,
                address = address.copy(
                    state = changes.address.state,
                    city = changes.address.city,
                    zip = changes.address.zip,
                    street = changes.address.street,
                    number = changes.address.number,
                    floor = changes.address.floor,
                    apartment = changes.address.apartment
                ),
                email = changes.email,
                phone = changes.phone,
                activity = changes.activity,
                category = changes.category,
                tax = tax.copy(
                    id = changes.tax.id,
                    type = changes.tax.type,
                )
            )
        }
}

