package com.menta.bff.devices.login.login.refresh.adapter.out

import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.refresh.adapter.out.model.RefreshClientRequest
import com.menta.bff.devices.login.login.refresh.adapter.out.model.mapper.ToRefreshClientRequestMapper
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
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

class RefreshClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val mapper = mockk<ToRefreshClientRequestMapper>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = RefreshClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toRefreshClientRequestMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("refresh token") {
        val uri = URI("")
        val refreshRequest = Refresh(
            token = "TOKEN",
            userType = MERCHANT
        )
        val refreshClientRequest = RefreshClientRequest(
            refreshToken = "TOKEN",
            userType = MERCHANT
        )

        scenario("client successful") {
            val response = aUserAuthResponseWithToken()

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(refreshClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(refreshRequest) } returns refreshClientRequest
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.just(response)

            client.refresh(refreshRequest) shouldBeRight response

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(refreshClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(refreshRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(refreshClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(refreshRequest) } returns refreshClientRequest
            every { responseSpec.bodyToMono(UserAuth::class.java) } returns Mono.error(error)

            client.refresh(refreshRequest) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(refreshClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(refreshRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(UserAuth::class.java) }
        }
    }
})
