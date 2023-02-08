package com.menta.bff.devices.login.login.revoke.adapter.out

import com.menta.bff.devices.login.login.revoke.adapter.out.model.RevokeTokenClientRequest
import com.menta.bff.devices.login.login.revoke.adapter.out.model.mapper.ToRevokeTokenClientRequestMapper
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unhandledException
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI

class RevokeTokenClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val mapper = mockk<ToRevokeTokenClientRequestMapper>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = RevokeTokenClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toRevokeTokenClientRequestMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("revoke token") {
        val uri = URI("")
        val revokeTokenRequest = RevokeToken(
            token = "TOKEN",
            userType = MERCHANT
        )

        val revokeTokenClientRequest = RevokeTokenClientRequest(
            refreshToken = "TOKEN",
            userType = MERCHANT
        )

        val entity = mockk<ResponseEntity<Void>>()

        scenario("client successful") {
            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(revokeTokenClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(revokeTokenRequest) } returns revokeTokenClientRequest
            every { responseSpec.toBodilessEntity() } returns Mono.just(entity)

            client.revoke(revokeTokenRequest) shouldBeRight Unit

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(revokeTokenClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(revokeTokenRequest) }
            verify(exactly = 1) { responseSpec.toBodilessEntity() }
        }

        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(revokeTokenClientRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom(revokeTokenRequest) } returns revokeTokenClientRequest
            every { responseSpec.toBodilessEntity() } returns Mono.error(error)

            client.revoke(revokeTokenRequest) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.bodyValue(revokeTokenClientRequest) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom(revokeTokenRequest) }
            verify(exactly = 1) { responseSpec.toBodilessEntity() }
        }
    }
})
