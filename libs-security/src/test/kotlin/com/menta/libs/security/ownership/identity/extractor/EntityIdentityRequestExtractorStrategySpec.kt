package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.BODY_ATTRIBUTE
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class EntityIdentityRequestExtractorStrategySpec : FeatureSpec({

    val extractor = mockk<EntityIdentityRequestExtractor>()
    val strategy = EntityIdentityRequestExtractorStrategy(mapOf(PATH_VARIABLE to extractor))

    feature("extract entity identity from request") {

        scenario("extractor not found") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, BODY_ATTRIBUTE, "name")

            shouldThrow<OwnerIdentityNotFoundException> { strategy.extract(request, owner) }

            verify(exactly = 0) { extractor.extract(request, owner) }
        }

        scenario("extractor doesnt return id") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "name")

            every { extractor.extract(request, owner) } returns null

            shouldThrow<OwnerIdentityNotFoundException> { strategy.extract(request, owner) }

            verify(exactly = 1) { extractor.extract(request, owner) }
        }

        scenario("extractor returns id") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "name")
            val id = UUID.randomUUID()

            every { extractor.extract(request, owner) } returns id

            strategy.extract(request, owner) shouldBe id

            verify(exactly = 1) { extractor.extract(request, owner) }
        }

    }

})