package com.menta.bff.devices.login.login.auth.adapter.out

import com.menta.bff.devices.login.login.aLoginRequest
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.aUserCredentials
import com.menta.bff.devices.login.login.auth.adapter.out.model.mapper.ToLoginClientRequestMapper
import com.menta.bff.devices.login.shared.domain.UserAuth
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

class LoginClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val mapper = mockk<ToLoginClientRequestMapper>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = LoginClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toLoginClientRequestMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("login") {
        val uri = URI("")
        val crendentials = aUserCredentials()
        val loginRequest = aLoginRequest()

        scenario("client successful") {
            val response = aUserAuthResponseWithToken()

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(loginRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(crendentials) } returns loginRequest
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.just(response)

            client.login(crendentials) shouldBeRight response

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(loginRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(crendentials) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(loginRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(crendentials) } returns loginRequest
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.error(error)

            client.login(crendentials) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(loginRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(crendentials) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
    }
})
