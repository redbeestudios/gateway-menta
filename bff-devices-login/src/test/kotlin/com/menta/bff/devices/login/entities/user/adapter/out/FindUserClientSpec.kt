package com.menta.bff.devices.login.entities.user.adapter.out

import com.menta.bff.devices.login.entities.user.aMerchantUser
import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
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

class FindUserClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val url = "an.url/users"
    val timeout = 500L

    val client = FindUserClient(
        webClient = webClient,
        url = url,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find user by email and type") {
        val email = "user@menta.global"
        val type = MERCHANT

        val uri = URI("$url/$email/type/$type")

        scenario("user found") {
            val user = aMerchantUser()

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(User::class.java) } returns Mono.just(user)

            client.findBy(email, type) shouldBeRight user

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(User::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(User::class.java) } returns Mono.error(error)

            client.findBy(email, type) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(User::class.java) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(User::class.java) } returns Mono.empty()

            client.findBy(email, type) shouldBeLeft notFound("user $email for $type not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(User::class.java) }
        }
    }
})
