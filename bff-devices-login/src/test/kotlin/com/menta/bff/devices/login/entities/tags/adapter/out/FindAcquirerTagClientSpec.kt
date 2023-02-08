package com.menta.bff.devices.login.entities.tags.adapter.out

import com.menta.bff.devices.login.entities.tags.anAcquirerTagsEmv
import com.menta.bff.devices.login.entities.tags.tagType
import com.menta.bff.devices.login.entities.user.customerId
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
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

class FindAcquirerTagClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val url = "an.url/tags"
    val timeout = 500L

    val client = FindAcquirerTagClient(
        webClient = webClient,
        url = url,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find acquirer emvs by customerId") {
        val customerId = customerId
        val type = tagType
        val userAuth = aUserAuthResponseWithToken()
        val uri = URI("$url?customerId=$customerId&type=$type")

        scenario("acquirers emvs found") {
            val acquirersEmvs = anAcquirerTagsEmv()
            val pageAcquirersEmvs = Page(acquirersEmvs)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(pageAcquirersEmvs)

            client.findBy(customerId, type, userAuth) shouldBeRight acquirersEmvs

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            client.findBy(customerId, type, userAuth) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.empty()

            client.findBy(customerId, type, userAuth) shouldBeLeft notFound("acquirers tags for customer id $customerId and type $type not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
    }
})
