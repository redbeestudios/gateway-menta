package com.menta.api.merchants.adapter.`in`.model.mapper

import com.menta.api.merchants.adapter.`in`.model.MerchantRequest
import com.menta.api.merchants.domain.PreMerchant
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToPreMerchantMapper {

    fun mapFrom(merchantRequest: MerchantRequest): PreMerchant =
        with(merchantRequest) {
            PreMerchant(
                customerId = customerId,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                representative = representative?.let {
                    PreMerchant.Representative(
                        representativeId = PreMerchant.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                businessOwner = businessOwner?.let {
                    PreMerchant.BusinessOwner(
                        birthDate = businessOwner.birthDate,
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        ownerId = PreMerchant.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                merchantCode = merchantCode,
                address = PreMerchant.Address(
                    address.state,
                    address.city,
                    address.zip,
                    address.street,
                    address.number,
                    address.floor,
                    address.apartment
                ),
                email = email,
                phone = phone,
                activity = activity,
                category = category,
                tax = PreMerchant.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                settlementCondition = PreMerchant.SettlementCondition(
                    settlementCondition.transactionFee,
                    settlementCondition.settlement,
                    settlementCondition.cbuOrCvu
                )
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
