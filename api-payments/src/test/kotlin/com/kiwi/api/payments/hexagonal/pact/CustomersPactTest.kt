package com.kiwi.api.payments.hexagonal.pact

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactFolder
import com.kiwi.api.payments.hexagonal.adapter.out.rest.CustomerRestClient
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToCustomerMapper
import com.kiwi.api.payments.hexagonal.application.aCustomerResponse
import io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import java.util.UUID

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "api-customers")
@PactFolder("build/pacts-pending")
class CustomersPactTest {

    @Pact(consumer = "api-payments", provider = "api-customers")
    fun paymentsInteractionCustomersPact(builder: PactDslWithProvider): RequestResponsePact =
        builder.given("customer ID is up")
            .uponReceiving("a request for customer ID with valid data ") {
                method("GET")
                path("/customers/2c6acc4f-14cd-445a-99a5-1d80c70cc6ed")
            }
            .willRespondWith {
                status(200)
                headers {
                    this["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
                }
                body(
                    newJsonBody { o ->
                        o.stringValue("id", "2c6acc4f-14cd-445a-99a5-1d80c70cc6ed")
                            .stringValue("country", "ARG")
                            .stringValue("legal_type", "NATURAL_PERSON")
                            .stringValue("business_name", "PedidosYa")
                            .stringValue("fantasy_name", "PEYA (GPS - Flujo Directo)")
                            .o("tax") {
                                stringValue("type", "something")
                                stringValue("id", "1123123")
                            }
                            .stringValue("activity", "comida")
                            .stringValue("email", "emix.lr@gmail.com")
                            .stringValue("phone", "111123213132")
                            .o("address") {
                                stringValue("state", "villa crespo")
                                stringValue("city", "Bs As")
                                stringValue("zip", "1889")
                                stringValue("street", "23")
                                stringValue("number", "27")
                                stringValue("floor", "")
                                stringValue("apartment", "")
                            }
                            .o("representative") {
                                o("representative_id") {
                                    stringValue("type", "dni")
                                    stringValue("number", "32456789")
                                }
                                stringValue("birth_date", "2022-03-31T15:01:01.999Z")
                                stringValue("name", "Soy un nombre de represent")
                                stringValue("surname", "soy un apellido de represen")
                            }
                            .o("businessOwner") {
                            }
                            .o("settlementCondition") {
                            }
                            .stringValue("status", "ACTIVE")
                    }.build()
                )
            }
            .toPact()

    @Test
    @PactTestFor(pactMethod = "paymentsInteractionCustomersPact")
    fun testPaymentsInteractionCustomersPact(mockServer: MockServer) {
        val id = UUID.fromString("2c6acc4f-14cd-445a-99a5-1d80c70cc6ed")
        val customerRestClient = customersRestClient(mockServer)
        assertEquals(aCustomerResponse, customerRestClient.retrieve(id))
    }

    private fun customersRestClient(mockserver: MockServer) = CustomerRestClient(
        toCustomerMapper = ToCustomerMapper(),
        url = mockserver.getUrl() + "/customers/{customerId}",
        timeout = 3000,
        webClient = webClient
    )
}
