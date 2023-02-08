package com.menta.bff.devices.login.entities.terminal.adapter.out

import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.terminals.adapter.out.FindTerminalClient
import com.menta.bff.devices.login.entities.terminals.adapter.out.Page
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
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

class FindTerminalClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = FindTerminalClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find terminal by serialCode") {
        val serialCode = "serialCode"
        val uri = URI("?serialCode=serialCode")

        scenario("terminal found") {
            val terminal = aTerminal()
            val page = Page(
                embedded = Page.Embedded(
                    listOf(terminal)
                )
            )

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(page)

            client.findBy(serialCode) shouldBeRight terminal

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            client.findBy(serialCode) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.empty()

            client.findBy(serialCode) shouldBeLeft notFound("terminal $serialCode not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
    }
})
