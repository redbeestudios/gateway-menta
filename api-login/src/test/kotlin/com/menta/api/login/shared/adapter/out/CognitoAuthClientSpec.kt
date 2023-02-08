package com.menta.api.login.shared.adapter.out

import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.login.aInitiateAuthRequest
import com.menta.api.login.aInitiateAuthResult
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.aUserCredentials
import com.menta.api.login.refresh.aRefresh
import com.menta.api.login.refresh.aUserPoolAwareRefresh
import com.menta.api.login.shared.adapter.out.mapper.ToInitiateAuthRequestMapper
import com.menta.api.login.shared.domain.mapper.ToUserAuthMapper
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.authenticationProviderError
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CognitoAuthClientSpec : FeatureSpec({

    val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
    val toUserAuth = mockk<ToUserAuthMapper>()
    val toInitiateAuthRequest = mockk<ToInitiateAuthRequestMapper>()

    val adapter = CognitoAuthClient(
        awsCognitoIdentityProvider,
        toUserAuth,
        toInitiateAuthRequest
    )

    beforeEach { clearAllMocks() }

    feature("login") {
        scenario("successful login") {
            val userCredentials = aUserCredentials()
            val userAuth = aUserAuthWithToken()
            val initiateAuthRequest = aInitiateAuthRequest()
            val initiateAuthResult = aInitiateAuthResult()

            every { toInitiateAuthRequest.mapFrom(userCredentials) } returns initiateAuthRequest.right()
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } returns initiateAuthResult
            every { toUserAuth.mapFrom(initiateAuthResult) } returns userAuth

            adapter.login(userCredentials) shouldBe userAuth.right()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toUserAuth.mapFrom(initiateAuthResult) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(userCredentials) }
        }
        scenario("cognito user error") {
            val userCredentials = aUserCredentials()
            val initiateAuthRequest = aInitiateAuthRequest()

            val ex = InvalidParameterException("invalid parameters")
            every { toInitiateAuthRequest.mapFrom(userCredentials) } returns initiateAuthRequest.right()
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } throws ex

            adapter.login(userCredentials) shouldBe unauthorizedUser(ex).left()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(userCredentials) }
            verify(exactly = 0) { toUserAuth.mapFrom(any<InitiateAuthResult>()) }
        }
        scenario("authentication provider error") {
            val userCredentials = aUserCredentials()
            val initiateAuthRequest = aInitiateAuthRequest()

            val ex = RuntimeException("error communicating with auth provider")
            every { toInitiateAuthRequest.mapFrom(userCredentials) } returns initiateAuthRequest.right()
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } throws ex

            adapter.login(userCredentials) shouldBe authenticationProviderError(ex).left()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(userCredentials) }
            verify(exactly = 0) { toUserAuth.mapFrom(any<InitiateAuthResult>()) }
        }
    }

    feature("refresh") {
        scenario("successful refresh") {
            val refresh = aUserPoolAwareRefresh()
            val userAuth = aUserAuthWithToken()
            val initiateAuthRequest = aInitiateAuthRequest()
            val initiateAuthResult = aInitiateAuthResult()

            every { toInitiateAuthRequest.mapFrom(refresh) } returns initiateAuthRequest
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } returns initiateAuthResult
            every { toUserAuth.mapFrom(initiateAuthResult) } returns userAuth

            adapter.refresh(refresh) shouldBe userAuth.right()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toUserAuth.mapFrom(initiateAuthResult) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(refresh) }
        }
        scenario("cognito error") {
            val refresh = aUserPoolAwareRefresh()
            val initiateAuthRequest = aInitiateAuthRequest()

            val ex = InvalidParameterException("invalid parameters")
            every { toInitiateAuthRequest.mapFrom(refresh) } returns initiateAuthRequest
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } throws ex

            adapter.refresh(refresh) shouldBe unauthorizedUser(ex).left()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(refresh) }
            verify(exactly = 0) { toUserAuth.mapFrom(any<InitiateAuthResult>()) }
        }
        scenario("authentication provider error") {
            val refresh = aUserPoolAwareRefresh()
            val initiateAuthRequest = aInitiateAuthRequest()

            val ex = RuntimeException("error communicating with auth provider")
            every { toInitiateAuthRequest.mapFrom(refresh) } returns initiateAuthRequest
            every { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) } throws ex

            adapter.refresh(refresh) shouldBe authenticationProviderError(ex).left()

            verify(exactly = 1) { awsCognitoIdentityProvider.initiateAuth(initiateAuthRequest) }
            verify(exactly = 1) { toInitiateAuthRequest.mapFrom(refresh) }
            verify(exactly = 0) { toUserAuth.mapFrom(any<InitiateAuthResult>()) }
        }
    }

})
