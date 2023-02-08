package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant.Address
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant.BusinessOwner
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant.Representative
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant.SettlementCondition
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
                    Representative(
                        id = Representative.RepresentativeId(
                            type = it.representativeId.type,
                            number = it.representativeId.number
                        ),
                        birthDate = it.birthDate,
                        name = it.name,
                        surname = it.surname
                    )
                },
                businessOwner = businessOwner?.let {
                    BusinessOwner(
                        birthDate = it.birthDate,
                        name = it.name,
                        surname = it.surname,
                        id = BusinessOwner.OwnerId(
                            type = it.ownerId.type,
                            number = it.ownerId.number
                        )
                    )
                },
                merchantCode = merchantCode,
                address = Address(
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
                tax = Merchant.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                settlementCondition = SettlementCondition(
                    settlementCondition.transactionFee,
                    settlementCondition.settlement,
                    settlementCondition.cbuOrCvu
                )
            )
        }
}
