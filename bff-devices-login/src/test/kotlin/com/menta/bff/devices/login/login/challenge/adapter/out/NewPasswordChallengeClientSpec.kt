package com.menta.bff.devices.login.login.challenge.adapter.out

import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.challenge.adapter.out.model.NewPasswordChallengeClientRequest
import com.menta.bff.devices.login.login.challenge.adapter.out.model.mapper.ToNewPasswordChallengeClientRequestMapper
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType.CUSTOMER
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unhandledException
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI

class NewPasswordChallengeClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val mapper = mockk<ToNewPasswordChallengeClientRequestMapper>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = NewPasswordChallengeClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toNewPasswordChallengeClientRequestMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("challenge token") {
        val uri = URI("")
        val challengeRequest = NewPasswordChallengeSolution(
            session = "a session",
            user = "user@user.com",
            userType = CUSTOMER,
            newPassword = "a new password"
        )
        val challengeClientRequest = NewPasswordChallengeClientRequest(
            session = "a session",
            user = "user@user.com",
            userType = CUSTOMER,
            newPassword = "a new password"
        )

        scenario("client successful") {
            val response = aUserAuthResponseWithToken()

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { mapper.mapFrom(challengeRequest) } returns challengeClientRequest
            every { requestSpec.bodyValue(challengeClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.just(response)

            client.solve(challengeRequest) shouldBeRight response

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(challengeClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(challengeRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(challengeClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(challengeRequest) } returns challengeClientRequest
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.error(error)

            client.solve(challengeRequest) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(challengeClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(challengeRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
    }
})
