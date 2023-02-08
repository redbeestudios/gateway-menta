package com.menta.api.merchants.merchant.adapter.`in`.model.mapper

import com.menta.api.merchants.aMerchantRequest
import com.menta.api.merchants.adapter.`in`.model.mapper.ToPreMerchantMapper
import com.menta.api.merchants.domain.PreMerchant
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreMerchantMapperSpec : FeatureSpec({

    val mapper = ToPreMerchantMapper()

    feature("map MerchantRequest to PreMerchant") {

        scenario("successful map with representative and businessOwner") {
            val merchantRequest = aMerchantRequest()

            mapper.mapFrom(merchantRequest) shouldBe
                with(merchantRequest) {
                    PreMerchant(
                        customerId = customerId,
                        country = country,
                        legalType = legalType,
                        businessName = businessName,
                        fantasyName = fantasyName,
                        representative = PreMerchant.Representative(
                            representativeId = PreMerchant.Representative.RepresentativeId(
                                type = representative!!.representativeId.type,
                                number = representative!!.representativeId.number
                            ),
                            birthDate = representative!!.birthDate,
                            name = representative!!.name,
                            surname = representative!!.surname
                        ),
                        businessOwner = PreMerchant.BusinessOwner(
                            birthDate = businessOwner!!.birthDate,
                            name = businessOwner!!.name,
                            surname = businessOwner!!.surname,
                            ownerId = PreMerchant.BusinessOwner.OwnerId(
                                type = businessOwner!!.ownerId.type,
                                number = businessOwner!!.ownerId.number
                            )
                        ),
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
                }
        }

        scenario("successful map without representative and businessOwner") {
            val merchantRequest = aMerchantRequest().copy(representative = null, businessOwner = null)

            mapper.mapFrom(merchantRequest) shouldBe
                with(merchantRequest) {
                    PreMerchant(
                        customerId = customerId,
                        country = country,
                        legalType = legalType,
                        businessName = businessName,
                        fantasyName = fantasyName,
                        representative = null,
                        businessOwner = null,
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
                }
        }
    }
})
