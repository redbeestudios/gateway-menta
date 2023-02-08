package com.menta.api.login.shared.adapter.out.mapper

import arrow.core.right
import com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest
import com.menta.api.login.aInitiateAuthRequest
import com.menta.api.login.aUserCredentials
import com.menta.api.login.aUserPool
import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import com.menta.api.login.shared.domain.UserType.MERCHANT
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToInitiateAuthRequestMapperSpec : FeatureSpec({

    val provider = mockk<UserPoolProvider>()
    val mapper = ToInitiateAuthRequestMapper(provider)

    feature("map userCredentials to initiateAuthRequest") {
        scenario("successful map") {
            val userCredentials = aUserCredentials()
            val userPool = aUserPool()
            val initiateAuthRequest = aInitiateAuthRequest()

            every { provider.provideFor(userCredentials.userType) } returns userPool.right()

            mapper.mapFrom(userCredentials) shouldBe initiateAuthRequest.right()

            verify(exactly = 1) { provider.provideFor(userCredentials.userType) }
        }
    }


    feature("map initiate auth request from user pool aware refresh") {
        val refresh = UserPoolAwareRefresh(
            refresh = Refresh(
                token = "a token",
                userType = MERCHANT
            ),
            userPool = Provider.UserPool(
                code = "a code",
                clientId = "a client id"
            )
        )

        val request = InitiateAuthRequest()
            .withAuthFlow(REFRESH_TOKEN)
            .withClientId("a client id")
            .addAuthParametersEntry("REFRESH_TOKEN", "a token")

        mapper.mapFrom(refresh) shouldBe request
    }
})
