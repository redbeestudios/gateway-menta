package com.menta.libs.security.ownership.identity.provider

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.identity.extractor.EntityIdentityRequestExtractorStrategy
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class DefaultOwnerIdentityProviderSpec : FeatureSpec({

    val strategy = mockk<EntityIdentityRequestExtractorStrategy>()
    val provider = DefaultOwnerIdentityProvider(strategy)

    beforeEach { clearAllMocks() }

    feature("provide owner identity") {

        scenario("owner identity found") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "a name")
            val id = UUID.randomUUID()

            every { strategy.extract(request, owner) } returns id

            provider.provideFrom(request, owner) shouldBe OwnerIdentity(id, owner.type)

            verify(exactly = 1) { strategy.extract(request, owner) }
        }

    }
})