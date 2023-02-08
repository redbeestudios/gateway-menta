package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
import javax.servlet.http.HttpServletRequest

class FromPathEntityIdentityRequestExtractorSpec : FeatureSpec({

    val extractor = DefaultFromPathEntityIdentityRequestExtractor()

    feature("identity extracted from path") {

        scenario("path variable not found") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")

            every { request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE) } returns null

            extractor.extract(request, owner) shouldBe null
        }

        scenario("attribute is not map") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")

            every { request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE) } returns "an attribute"

            extractor.extract(request, owner) shouldBe null
        }

        scenario("attribute found") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")
            val id = "194bb2dc-b814-4c05-9150-69fa7bdf530b"

            every { request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE) } returns mapOf("merchantId" to id)

            extractor.extract(request, owner) shouldBe UUID.fromString(id)
        }

    }
})
