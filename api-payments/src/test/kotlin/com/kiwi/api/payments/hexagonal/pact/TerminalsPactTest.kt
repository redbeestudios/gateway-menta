package com.kiwi.api.payments.hexagonal.pact

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactFolder
import com.kiwi.api.payments.hexagonal.adapter.out.rest.TerminalRestClient
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToTerminalMapper
import com.kiwi.api.payments.hexagonal.application.aTerminalResponse
import io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import java.util.UUID

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "api-terminals")
@PactFolder("build/pacts-pending")
class TerminalsPactTest {

    @Pact(consumer = "api-payments", provider = "api-terminals")
    fun paymentsInteractionTerminalsPact(builder: PactDslWithProvider): RequestResponsePact =
        builder.given("terminal ID is up")
            .uponReceiving("a request for terminal ID with valid data ") {
                method("GET")
                path("/terminals/0ce8dc16-da32-4dbb-9acc-c29ed52c6aca")
            }
            .willRespondWith {
                status(200)
                headers {
                    this["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
                }
                body(
                    newJsonBody { o ->
                        o.stringValue("id", "0ce8dc16-da32-4dbb-9acc-c29ed52c6aca")
                            .stringValue("merchant_id", "3f30f5f7-c85a-4769-ae99-a76545895481")
                            .stringValue("customer_id", "2c6acc4f-14cd-445a-99a5-1d80c70cc6ed")
                            .stringValue("serial_code", "50010003")
                            .stringValue("hardware_version", "10")
                            .stringValue("trade_mark", "pirutchit")
                            .stringValue("model", "zg300")
                            .stringValue("status", "ACTIVE")
                            .array("features") { a ->
                                a.stringValue("MANUAL")
                                    .stringValue("STRIPE")
                                    .stringValue("CHIP")
                            }
                    }.build()
                )
            }
            .toPact()

    @Test
    @PactTestFor(pactMethod = "paymentsInteractionTerminalsPact")
    fun testPaymentsInteractionTerminalsPact(mockServer: MockServer) {

        val id = UUID.fromString("0ce8dc16-da32-4dbb-9acc-c29ed52c6aca")
        val terminalsRestClient = terminalsRestClient(mockServer)

        assertEquals(aTerminalResponse, terminalsRestClient.retrieve(id))
    }

    private fun terminalsRestClient(mockserver: MockServer) = TerminalRestClient(
        toTerminalMapper = ToTerminalMapper(),
        url = mockserver.getUrl() + "/terminals/{terminalId}",
        timeout = 3000,
        webClient = webClient
    )
}
