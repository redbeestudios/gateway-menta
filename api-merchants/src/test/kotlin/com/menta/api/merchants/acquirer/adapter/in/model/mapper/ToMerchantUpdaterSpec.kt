package com.menta.api.merchants.acquirer.adapter.`in`.model.mapper

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.aMerchantRequest
import com.menta.api.merchants.aUpdateRequest
import com.menta.api.merchants.adapter.`in`.model.UpdateRequest
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantUpdater
import com.menta.api.merchants.domain.Country.COL
import com.menta.api.merchants.domain.State.CORDOBA
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToMerchantUpdaterSpec : FeatureSpec({

    val mapper = ToMerchantUpdater()

    feature("map merchantRequest to Merchant") {

        scenario("successful map") {
            val merchant = aMerchant().copy(businessOwner = null)
            val request = aMerchantRequest().copy(businessOwner = null)

            mapper.applyChanges(merchant, request) shouldBe merchant.copy(
                representative = merchant.representative?.representativeId?.let {
                    merchant.representative?.copy(
                        name = "Jorge",
                        representativeId = it.copy(
                            type = "id",
                            number = "12313"
                        )
                    )
                },
                merchantCode = "a code",
                address = merchant.address.copy(
                    floor = "",
                    apartment = ""
                ),
                email = "email@gmail.com",
                phone = "112345678900",
                activity = "a activity"
            )
        }
    }

    feature("map updateMerchant to Merchant") {

        scenario("successful map whit all fields") {
            val merchant = aMerchant().copy(businessOwner = null)
            val request = aUpdateRequest()

            mapper.applyChanges(merchant, request) shouldBe merchant.copy(
                businessName = "name",
                fantasyName = "name",
                representative = merchant.representative?.representativeId?.let {
                    merchant.representative?.copy(
                        name = "Pedro",
                        surname = "Lopez",
                        representativeId = it.copy(
                            type = "CUIL",
                            number = "99999999999"
                        )
                    )
                },
                merchantCode = "a new code",
                address = merchant.address.copy(
                    state = CORDOBA,
                    city = "Cordoba capital",
                    zip = "11111",
                    street = "Av. Cordoba",
                    number = "11111",
                    floor = null,
                    apartment = null
                ),
                email = "john@doe.com",
                phone = "88888888",
                activity = "the activity",
                category = "category",
                tax = merchant.tax.copy(
                    id = "99999999999", "MONOTRIBUTISTA"
                ),
                settlementCondition = merchant.settlementCondition.copy(
                    transactionFee = "a new fee",
                    settlement = "a new settlement",
                    cbuOrCvu = "99999999999"
                )
            )
        }

        scenario("successful map whit one fields") {
            val merchant = aMerchant().copy(businessOwner = null)
            val request = UpdateRequest(country = COL)

            mapper.applyChanges(merchant, request) shouldBe merchant.copy(
                country = COL
            )
        }
    }
})
