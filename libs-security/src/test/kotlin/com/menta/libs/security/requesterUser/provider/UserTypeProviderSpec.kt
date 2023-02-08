package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer
import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class UserTypeProviderSpec : FeatureSpec({

    val properties = mockk<SecurityConfigurationProperties>()
    val provider = UserTypeProvider(properties)

    beforeEach { clearAllMocks() }

    feature("provide for issuer") {

        scenario("userType found") {
            val issuerUri = "an uri"
            val resourceServer = mockk<ResourceServer>()
            val issuers = listOf(Issuer(name = "merchant", uri = "an uri", authoritiesClaimKey = "a claim key"))

            every { properties.resourceServer } returns resourceServer
            every { resourceServer.issuers } returns issuers

            provider.provideFor(issuerUri) shouldBe MERCHANT
        }

        scenario("resource server missing") {
            val issuerUri = "an uri"
            val resourceServer = null

            every { properties.resourceServer } returns resourceServer

            provider.provideFor(issuerUri) shouldBe null
        }

        scenario("issuer missing") {
            val issuerUri = "an uri"
            val resourceServer = mockk<ResourceServer>()
            val issuers = null

            every { properties.resourceServer } returns resourceServer
            every { resourceServer.issuers } returns issuers

            provider.provideFor(issuerUri) shouldBe null
        }

        scenario("issuer empty") {
            val issuerUri = "an uri"
            val resourceServer = mockk<ResourceServer>()
            val issuers = emptyList<Issuer>()

            every { properties.resourceServer } returns resourceServer
            every { resourceServer.issuers } returns issuers

            provider.provideFor(issuerUri) shouldBe null
        }

        scenario("no issuer found") {
            val issuerUri = "an uri"
            val resourceServer = mockk<ResourceServer>()
            val issuers = listOf(Issuer(name = "a name", uri = "another uri", authoritiesClaimKey = "a claim key"))

            every { properties.resourceServer } returns resourceServer
            every { resourceServer.issuers } returns issuers

            provider.provideFor(issuerUri) shouldBe null
        }
    }
})
