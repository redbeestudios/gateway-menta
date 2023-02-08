package com.menta.api.login.refresh.domain.mapper

import com.menta.api.login.aUserPool
import com.menta.api.login.refresh.aRefresh
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserPoolAwareRefreshMapperSpec : FeatureSpec({

    val mapper = ToUserPoolAwareRefreshMapper()

    feature("map user pool aware refresh from user pool and refresh") {

        scenario("map") {
            val userPool = aUserPool()
            val refresh = aRefresh()

            mapper.mapFrom(refresh, userPool) shouldBe UserPoolAwareRefresh(refresh, userPool)
        }
    }
}) {
}