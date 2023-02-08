package com.menta.apisecrets.adapter.out.repository

import com.menta.apisecrets.adapter.out.UriProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.net.URI

class UriProviderSpec : FeatureSpec({

    val host = "api-internal.dev.apps.menta.global"
    val scheme = "https"
    val terminalPath = "/terminals"
    val customerPath = "/customers"

    val provider = UriProvider(
        host = host,
        scheme = scheme,
        terminalPath = terminalPath,
        customerPath = customerPath
    )

    feature("provide for terminals") {

        scenario("uri provided") {
            provider.provideForTerminals(mapOf("param" to "aParam")) shouldBe
                URI(scheme, host, terminalPath, "param=aParam", null)
        }
    }

    feature("provide for customers") {

        scenario("uri provided") {
            provider.provideForCustomer("anId") shouldBe
                URI(scheme, host, "$customerPath/anId", null, null)
        }
    }
})
