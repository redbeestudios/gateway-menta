package com.menta.libs.security.requesterUser.model.mapper

import com.menta.libs.security.requesterUser.exception.InvalidRequesterUserException
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import com.menta.libs.security.requesterUser.provider.UserTypeProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

class ToRequesterUserMapperSpec : FeatureSpec({

    val userTypeProvider = mockk<UserTypeProvider>()
    val mapper = ToRequesterUserMapper(userTypeProvider)

    beforeEach { clearAllMocks() }

    feature("map") {

        scenario("mapped") {
            val jwt = mockk<Jwt>()

            every { jwt.claims } returns mutableMapOf(
                "email" to "email@menta.global",
                "iss" to "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag",
                "customer_id" to "21292eb4-dfff-4c2a-b00a-3d99b9e0f4d0",
                "merchant_id" to "71880fba-2817-4518-b19b-5690a1babc83",

            ) as Map<String, Any>?

            every { userTypeProvider.provideFor("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag") } returns
                MERCHANT

            mapper.from(jwt) shouldBe RequesterUser(
                type = MERCHANT,
                name = "email@menta.global",
                attributes = RequesterUser.Attributes(
                    customerId = UUID.fromString("21292eb4-dfff-4c2a-b00a-3d99b9e0f4d0"),
                    merchantId = UUID.fromString("71880fba-2817-4518-b19b-5690a1babc83"),
                    email = "email@menta.global"
                )
            )
        }

        scenario("non required fields missing") {
            val jwt = mockk<Jwt>()

            every { jwt.claims } returns mutableMapOf(
                "email" to "email@menta.global",
                "iss" to "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag"
            ) as Map<String, Any>?

            every { userTypeProvider.provideFor("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag") } returns
                MERCHANT

            mapper.from(jwt) shouldBe RequesterUser(
                type = MERCHANT,
                name = "email@menta.global",
                attributes = RequesterUser.Attributes(
                    customerId = null,
                    merchantId = null,
                    email = "email@menta.global"
                )
            )
        }

        scenario("required fields missing: email") {
            val jwt = mockk<Jwt>()

            every { jwt.claims } returns mutableMapOf(
                "iss" to "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag"
            ) as Map<String, Any>?

            every { userTypeProvider.provideFor("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TllFC1bag") } returns
                MERCHANT

            shouldThrow<InvalidRequesterUserException> { mapper.from(jwt) }
        }

        scenario("required fields missing: iss") {
            val jwt = mockk<Jwt>()

            every { jwt.claims } returns mutableMapOf(
                "email" to "email@menta.global"
            ) as Map<String, Any>?

            shouldThrow<InvalidRequesterUserException> { mapper.from(jwt) }
        }
    }
})
