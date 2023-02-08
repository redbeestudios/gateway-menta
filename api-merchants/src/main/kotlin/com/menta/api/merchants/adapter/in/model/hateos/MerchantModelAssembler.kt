package com.menta.api.merchants.adapter.`in`.model.hateos

import com.menta.api.merchants.adapter.`in`.model.MerchantResponse
import com.menta.api.merchants.domain.LegalType
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.provider.AppUrlProvider
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class MerchantModelAssembler(
    private val appUrlProvider: AppUrlProvider
) : RepresentationModelAssembler<Merchant, MerchantResponse> {
    override fun toModel(merchant: Merchant): MerchantResponse =
        with(merchant) {
            MerchantResponse(
                id = id,
                customerId = customerId.toString(),
                country = country,
                legalType = LegalType.valueOf(legalType.toString()),
                businessName = businessName,
                fantasyName = fantasyName,
                representative = representative?.let {
                    MerchantResponse.Representative(
                        representativeId = MerchantResponse.Representative.RepresentativeId(
                            type = it.representativeId.type,
                            number = it.representativeId.number
                        ),
                        birthDate = it.birthDate,
                        name = it.name,
                        surname = it.surname
                    )
                },
                businessOwner = businessOwner?.let {
                    MerchantResponse.BusinessOwner(
                        birthDate = it.birthDate,
                        name = it.name,
                        surname = it.surname,
                        ownerId = MerchantResponse.BusinessOwner.OwnerId(
                            type = it.ownerId.type,
                            number = it.ownerId.number
                        )
                    )
                },
                merchantCode = merchantCode,
                address = MerchantResponse.Address(
                    address.state.name,
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
                tax = MerchantResponse.Tax(
                    id = tax.id,
                    type = tax.type
                ),
                settlementCondition = MerchantResponse.SettlementCondition(
                    settlementCondition.transactionFee,
                    settlementCondition.settlement,
                    settlementCondition.cbuOrCvu
                ),
                createDate = createDate,
                updateDate = updateDate,
                deleteDate = deleteDate
            ).add(
                Link.of(
                    "${appUrlProvider.provide()}/$id"
                ).withSelfRel()
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
