package com.menta.libs.security.ownership.identity.extractor

import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.BODY_ATTRIBUTE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class FromBodyEntityIdentityRequestExtractorSpec : FeatureSpec({

    val objectMapper = mockk<ObjectMapper>()
    val extractor = DefaultFromBodyEntityIdentityRequestExtractor(objectMapper)

    beforeEach { clearAllMocks() }

    feature("extract identity from body request") {

        scenario("empty request body") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, BODY_ATTRIBUTE, "a name")

            every {
                objectMapper.readValue(request.inputStream, Map::class.java)
            } returns emptyMap<Any, Any>()

            extractor.extract(request, owner) shouldBe null
        }

        scenario("argument not found in body") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, BODY_ATTRIBUTE, "a name")

            every {
                objectMapper.readValue(request.inputStream, Map::class.java)
            } returns mapOf("anoter key" to "another value")

            extractor.extract(request, owner) shouldBe null
        }

        scenario("argument found in body") {
            val request = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, BODY_ATTRIBUTE, "a name")
            val id = "064b2939-879a-4ffc-ae9d-80063b13ce7e"

            every {
                objectMapper.readValue(request.inputStream, Map::class.java)
            } returns mapOf("a name" to id)

            extractor.extract(request, owner) shouldBe UUID.fromString("064b2939-879a-4ffc-ae9d-80063b13ce7e")
        }


    }

})