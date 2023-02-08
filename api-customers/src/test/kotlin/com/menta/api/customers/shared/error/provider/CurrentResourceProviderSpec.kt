package com.menta.api.customers.shared.error.provider

import com.menta.api.customers.shared.error.providers.CurrentResourceProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import javax.servlet.http.HttpServletRequest

class CurrentResourceProviderSpec : FeatureSpec({

    val request = mockk<HttpServletRequest>()
    val provider = CurrentResourceProvider(httpServletRequest = request)

    feature("find uri") {
        scenario("uri found") {
            val uri = "an uri"
            every { request.requestURI } returns uri
            provider.provideUri() shouldBe uri
        }
    }

    feature("find query") {
        scenario("query found") {
            val query = "an query"
            every { request.queryString } returns query
            provider.provideQuery() shouldBe query
        }
    }
})
