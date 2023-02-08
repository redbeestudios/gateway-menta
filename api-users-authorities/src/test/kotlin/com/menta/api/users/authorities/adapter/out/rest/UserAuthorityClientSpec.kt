package com.menta.api.users.authorities.adapter.out.rest

import com.menta.api.users.authorities.adapter.out.rest.mapper.ToAssignAuthorityUserRequestMapper
import com.menta.api.users.authorities.adapter.out.rest.model.AssignAuthorityUserRequest
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.email
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.timeoutError
import com.menta.api.users.authorities.shared.other.error.model.exception.ApplicationErrorException
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

class UserAuthorityClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val mapper = mockk<ToAssignAuthorityUserRequestMapper>()
    val url = "an.url/users"
    val timeout = 500L

    val client = UserAuthorityClient(
        webClient = webClient,
        url = url,
        timeout = timeout,
        toAssignAuthorityUserRequestMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("assign authority by email and type") {
        val email = email
        val type = UserType.MERCHANT
        val uri = URI("$url/$email/type/$type/groups")
        val assignUserAuthority = anUserAssignAuthority
        val assignAuthorityUserRequest = AssignAuthorityUserRequest(
            name = "Payment::Create"
        )

        scenario("assign authority successfully") {
            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(assignAuthorityUserRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { mapper.mapFrom("Payment::Create") } returns assignAuthorityUserRequest
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.just(Unit)

            client.assign(assignUserAuthority) shouldBeRight Unit

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom("Payment::Create") }
            verify(exactly = 1) { requestSpec.bodyValue(assignAuthorityUserRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { requestSpec.bodyValue(assignAuthorityUserRequest) } returns requestSpec
            every { mapper.mapFrom("Payment::Create") } returns assignAuthorityUserRequest
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.error(error)

            client.assign(assignUserAuthority) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom("Payment::Create") }
            verify(exactly = 1) { requestSpec.bodyValue(assignAuthorityUserRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }
        scenario("client returned timeout error") {
            val error = ApplicationErrorException(
                timeoutError("Connection timeout while assign authority in User Api")
            )

            every { webClient.post() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.bodyValue(assignAuthorityUserRequest) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Unit::class.java) } returns Mono.error(error)
            every { mapper.mapFrom("Payment::Create") } returns assignAuthorityUserRequest

            shouldThrow<ApplicationErrorException> { client.assign(assignUserAuthority) }

            verify(exactly = 1) { webClient.post() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { mapper.mapFrom("Payment::Create") }
            verify(exactly = 1) { requestSpec.bodyValue(assignAuthorityUserRequest) }
            verify(exactly = 1) { responseSpec.bodyToMono(Unit::class.java) }
        }
    }
})
