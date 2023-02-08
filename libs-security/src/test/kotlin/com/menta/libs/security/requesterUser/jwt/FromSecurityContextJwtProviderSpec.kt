package com.menta.libs.security.requesterUser.jwt

import com.menta.libs.security.requesterUser.jwt.exception.MissingJWTException
import com.menta.libs.security.requesterUser.jwt.provider.FromSecurityContextJwtProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class FromSecurityContextJwtProviderSpec : FeatureSpec({

    val provider = FromSecurityContextJwtProvider()
    beforeEach { clearAllMocks() }

    feature("provide") {

        scenario("jwt provided") {
            val context = mockk<SecurityContext>()
            val authentication = mockk<JwtAuthenticationToken>()
            val jwt = mockk<Jwt>()

            mockkStatic(SecurityContextHolder::class)

            every { SecurityContextHolder.getContext() } returns context
            every { context.authentication } returns authentication
            every { authentication.principal } returns jwt

            provider.provide() shouldBe jwt

            verify(exactly = 1) { context.authentication }
            verify(exactly = 1) { authentication.principal }
        }

        scenario("context doesnt hold an authentication") {
            val context = mockk<SecurityContext>()
            mockkStatic(SecurityContextHolder::class)

            every { SecurityContextHolder.getContext() } returns context
            every { context.authentication } returns null

            shouldThrow<MissingJWTException> { provider.provide() }

            verify(exactly = 1) { context.authentication }
        }

        scenario("authentication isnt a jwt") {
            val context = mockk<SecurityContext>()
            val authentication = mockk<BearerTokenAuthentication>()

            mockkStatic(SecurityContextHolder::class)

            every { SecurityContextHolder.getContext() } returns context
            every { context.authentication } returns authentication

            shouldThrow<MissingJWTException> { provider.provide() }

            verify(exactly = 1) { context.authentication }
            verify(exactly = 0) { authentication.principal }
        }

        scenario("authentication doesnt hold a jwt") {
            val context = mockk<SecurityContext>()
            val authentication = mockk<JwtAuthenticationToken>()

            mockkStatic(SecurityContextHolder::class)

            every { SecurityContextHolder.getContext() } returns context
            every { context.authentication } returns authentication
            every { authentication.principal } returns null

            shouldThrow<MissingJWTException> { provider.provide() }

            verify(exactly = 1) { context.authentication }
            verify(exactly = 1) { authentication.principal }
        }
    }
})
