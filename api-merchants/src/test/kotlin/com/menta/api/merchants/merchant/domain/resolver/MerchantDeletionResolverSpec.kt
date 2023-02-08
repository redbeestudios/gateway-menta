package com.menta.api.merchants.merchant.domain.resolver

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.domain.resolver.MerchantDeletionResolver
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldNotBe

class MerchantDeletionResolverSpec : FeatureSpec({

    val resolver = MerchantDeletionResolver()

    feature("resolveDeletion") {

        scenario("with merchant") {
            resolver.resolveDeletion(
                aMerchant()
            ).deleteDate shouldNotBe null
        }
    }
})
