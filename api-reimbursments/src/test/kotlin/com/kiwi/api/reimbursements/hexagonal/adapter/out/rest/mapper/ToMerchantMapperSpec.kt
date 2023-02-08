package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.application.aMerchant
import com.kiwi.api.reimbursements.hexagonal.application.aMerchantResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToMerchantMapperSpec : FeatureSpec({

    feature("map merchant") {

        val mapper = ToMerchantMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val merchantResponse = aMerchantResponse()
            val merchant = aMerchant()

            mapper.map(merchantResponse) shouldBe with(merchant) {
                Merchant(
                    id = id,
                    customerId = customerId,
                    country = country,
                    legalType = legalType,
                    businessName = businessName,
                    fantasyName = fantasyName,
                    representative = representative,
                    businessOwner = businessOwner,
                    merchantCode = merchantCode,
                    address = address,
                    email = email,
                    phone = phone,
                    activity = activity,
                    category = category,
                    tax = tax,
                    settlementCondition = settlementCondition
                )
            }
        }
    }
})
