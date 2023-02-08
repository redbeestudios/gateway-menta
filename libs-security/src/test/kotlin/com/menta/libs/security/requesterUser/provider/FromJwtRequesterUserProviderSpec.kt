package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.requesterUser.exception.InvalidRequesterUserException
import com.menta.libs.security.requesterUser.jwt.exception.MissingJWTException
import com.menta.libs.security.requesterUser.jwt.provider.JwtProvider
import com.menta.libs.security.requesterUser.model.mapper.ToRequesterUserMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.oauth2.jwt.Jwt

class FromJwtRequesterUserProviderSpec : FeatureSpec({
    val jwtProvider = mockk<JwtProvider>()
    val mapper = mockk<ToRequesterUserMapper>()

    val provider = FromJwtRequesterUserProvider(jwtProvider = jwtProvider, toRequesterUserMapper = mapper)

    beforeEach { clearAllMocks() }

    feature("provide") {

        scenario("requester user provided") {
            val jwt = mockk<Jwt>()
            val user = aRequesterUser()

            every { jwtProvider.provide() } returns jwt
            every { mapper.from(jwt) } returns user

            provider.provide() shouldBe user

            verify(exactly = 1) { jwtProvider.provide() }
            verify(exactly = 1) { mapper.from(jwt) }
        }

        scenario("error while mapping") {
            val jwt = mockk<Jwt>()

            every { jwtProvider.provide() } returns jwt
            every { mapper.from(jwt) } throws InvalidRequesterUserException("a field")

            shouldThrow<InvalidRequesterUserException> {
                provider.provide()
            }

            verify(exactly = 1) { jwtProvider.provide() }
            verify(exactly = 1) { mapper.from(jwt) }
        }

        scenario("error while mapping") {
            val jwt = mockk<Jwt>()

            every { jwtProvider.provide() } returns jwt
            every { mapper.from(jwt) } throws InvalidRequesterUserException("a field")

            shouldThrow<InvalidRequesterUserException> {
                provider.provide()
            }

            verify(exactly = 1) { jwtProvider.provide() }
            verify(exactly = 1) { mapper.from(jwt) }
        }

        scenario("jwt not found") {

            every { jwtProvider.provide() } throws MissingJWTException()

            shouldThrow<MissingJWTException> {
                provider.provide()
            }

            verify(exactly = 1) { jwtProvider.provide() }
            verify(exactly = 0) { mapper.from(any()) }
        }
    }
})
