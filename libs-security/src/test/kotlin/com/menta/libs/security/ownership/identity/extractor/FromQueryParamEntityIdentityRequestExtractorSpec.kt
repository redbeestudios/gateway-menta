package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.QUERY_PARAMETER
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class FromQueryParamEntityIdentityRequestExtractorSpec : FeatureSpec({

    val extractor = DefaultFromQueryParamEntityIdentityRequestExtractor()

    feature("identity extracted from query param") {

        scenario("param not found") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, QUERY_PARAMETER, "argumentName")

            every { request.getParameterValues(owner.argumentName) } returns emptyArray()

            extractor.extract(request, owner) shouldBe null
        }

        scenario("id extracted") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, QUERY_PARAMETER, "argumentName")
            val id = "0d6eb78f-01eb-4f86-b689-66b640c0b186"
            every { request.getParameterValues(owner.argumentName) } returns arrayOf(id)

            extractor.extract(request, owner) shouldBe UUID.fromString(id)
        }

    }
})
