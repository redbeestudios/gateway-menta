package com.menta.apisecrets.adapter.out.repository

import com.menta.apisecrets.aTerminal
import com.menta.apisecrets.aTerminalResponse
import com.menta.apisecrets.adapter.out.Page
import com.menta.apisecrets.adapter.out.Page.Embedded
import com.menta.apisecrets.adapter.out.TerminalRepository
import com.menta.apisecrets.adapter.out.UriProvider
import com.menta.apisecrets.adapter.out.model.TerminalResponse
import com.menta.apisecrets.adapter.out.model.mapper.ToTerminalMapper
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.terminalNotFound
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.terminalRepositoryError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URI
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class TerminalRepositorySpec : FeatureSpec({

    feature("find terminal") {

        val mapper = mockk<ToTerminalMapper>()
        val webClient = mockk<WebClient>()
        val uriProvider = mockk<UriProvider>()
        val timeout = 500L

        val repository = TerminalRepository(
            toTerminalMapper = mapper,
            webClient = webClient,
            uriProvider = uriProvider,
            timeout = timeout
        )

        val spec = mockk<WebClient.RequestHeadersUriSpec<*>>()
        val responseSpec = mockk<WebClient.ResponseSpec>()
        beforeEach { clearAllMocks() }

        scenario("terminal found") {
            val serialCode = "testSerialCode"
            val response = Page(Embedded(listOf(aTerminalResponse(serialCode))))
            val terminal = aTerminal(serialCode)
            val uri = URI("")

            every { webClient.get() } returns spec
            every { uriProvider.provideForTerminals(any()) } returns uri
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(response)
            every { mapper.mapFrom(response.embedded.terminals.first()) } returns terminal

            repository.execute(serialCode) shouldBeRight listOf(terminal)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForTerminals(mapOf("serialCode" to serialCode)) }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
            verify(exactly = 1) { mapper.mapFrom(response.embedded.terminals.first()) }
        }

        scenario("empty list found") {
            val serialCode = "testSerialCode"
            val uri = URI("")

            every { webClient.get() } returns spec
            every { uriProvider.provideForTerminals(any()) } returns uri
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(Page(Embedded(emptyList())))

            repository.execute(serialCode) shouldBeLeft terminalNotFound(serialCode)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForTerminals(mapOf("serialCode" to serialCode)) }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }

        scenario("client returned 404") {
            val serialCode = "testSerialCode"
            val uri = URI("")
            val error = WebClientResponseException(404, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { uriProvider.provideForTerminals(any()) } returns uri
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            repository.execute(serialCode) shouldBeLeft terminalNotFound(serialCode)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForTerminals(mapOf("serialCode" to serialCode)) }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }

        scenario("client returned error") {
            val serialCode = "testSerialCode"
            val response = aTerminalResponse(serialCode)
            val uri = URI("")
            val error = WebClientResponseException(400, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { uriProvider.provideForTerminals(any()) } returns uri
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            repository.execute(serialCode) shouldBeLeft terminalRepositoryError()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForTerminals(mapOf("serialCode" to serialCode)) }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
            verify(exactly = 0) { mapper.mapFrom(response) }
        }

        scenario("timeout") {
            val serialCode = "testSerialCode"
            val response = aTerminalResponse(serialCode)
            val uri = URI("")
            val error = WebClientResponseException(408, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { uriProvider.provideForTerminals(any()) } returns uri
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            repository.execute(serialCode) shouldBeLeft ApplicationError.timeout()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForTerminals(mapOf("serialCode" to serialCode)) }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
            verify(exactly = 0) { mapper.mapFrom(response) }
        }
    }
})
