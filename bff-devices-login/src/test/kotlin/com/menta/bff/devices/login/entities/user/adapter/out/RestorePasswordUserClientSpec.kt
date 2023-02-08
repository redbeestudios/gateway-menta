package com.menta.bff.devices.login.entities.user.adapter.out

import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import com.menta.bff.devices.login.shared.other.error.model.exception.ApplicationErrorException
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI

class RestorePasswordUserClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val url = "an.url/users"
    val timeout = 500L

    val client = RestorePasswordUserClient(
        webClient = webClient,
        url = url,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("restore user password by email and type") {
        val email = "user@menta.global"
        val type = MERCHANT
        val uri = URI("$url/$email/type/$type/forgot-password")

        scenario("restore successfully") {

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.just(Unit)

            client.resolve(email, type) shouldBeRight Unit

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }

        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.error(error)

            client.resolve(email, type) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }

        scenario("client returned timeout error") {
            val error = ApplicationErrorException(timeoutError("Connection timeout while obtaining the user in User Api"))

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.error(error)

            shouldThrow<ApplicationErrorException> { client.resolve(email, type) }

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }
    }
})
