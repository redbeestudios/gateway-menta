package com.menta.apisecrets.adapter.controller

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.apisecrets.adapter.`in`.controller.SecretsController
import com.menta.apisecrets.adapter.`in`.controller.mapper.ToSecretResponseMapper
import com.menta.apisecrets.adapter.`in`.controller.model.SecretResponse
import com.menta.apisecrets.application.port.`in`.FindSecretInPort
import com.menta.apisecrets.application.port.out.TerminalUpdateProducerOutPort
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.shared.error.model.ApiError
import com.menta.apisecrets.shared.error.model.ApiErrorResponse
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.secretNotFound
import com.menta.apisecrets.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class SecretsControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val toSecretResponseMapper: ToSecretResponseMapper = mockk()
    val findSecretInPort: FindSecretInPort = mockk()
    val terminalProducer = mockk<TerminalUpdateProducerOutPort>()
    val errorResponseProvider: ErrorResponseProvider = mockk()

    val controller = SecretsController(findSecretInPort, toSecretResponseMapper, terminalProducer, errorResponseProvider)

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
        .build<ObjectMapper>()
        .let { MappingJackson2HttpMessageConverter(it) }

    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(objectMapper)
            .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/public/terminals/1234/secrets"
        every { httpServletRequest.queryString } returns null
    }

    feature("Get Secret") {

        scenario("Successful search") {
            val request = "1234"
            val secret = Secret("main", "ksn")
            val secrets = Secrets(listOf(secret), context = mockk())
            val applicationResponse: Either<ApplicationError, Secrets> = secrets.right()
            val secretResponse = SecretResponse.Secret(master = "master", ksn = "ksn")
            val contextResponse = SecretResponse.Context(
                terminal = SecretResponse.Context.Terminal(
                    id = UUID.randomUUID(),
                    serialCode = "a serialCode"
                )
            )
            val response = SecretResponse(context = contextResponse, listOf(secretResponse))

            every { findSecretInPort.execute(request) } returns applicationResponse
            every { toSecretResponseMapper.map(secrets) } returns response
            every { terminalProducer.produce(secrets) } returns Unit.right()

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/public/terminals/1234/secrets"
                )
                    .header("api-key", "someKey")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)

            verify(exactly = 1) { findSecretInPort.execute(request) }
            verify(exactly = 1) { toSecretResponseMapper.map(secrets) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("secret not found") {
            val request = "1234"
            val error = secretNotFound()

            every { findSecretInPort.execute(request) } returns error.left()
            every { errorResponseProvider.provideFor(error) } returns ApiErrorResponse(
                datetime = OffsetDateTime.MAX,
                errors = listOf(
                    ApiError(
                        code = "200",
                        resource = "resoruce",
                        message = "404",
                        metadata = mapOf("1" to "1")
                    )
                )
            )

            mockMvc.perform(
                MockMvcRequestBuilders.get(
                    "/public/terminals/1234/secrets"
                )
                    .header("api-key", "someKey")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound)
        }
    }
})
