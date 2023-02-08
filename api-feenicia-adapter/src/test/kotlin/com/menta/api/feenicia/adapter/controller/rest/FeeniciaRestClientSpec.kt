package com.menta.api.feenicia.adapter.controller.rest

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.feenicia.adapter.rest.FeeniciaRestClient
import com.menta.api.feenicia.adapter.rest.mapper.ToCreatedOperationMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaRefundRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaReverseRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToRequestHeaderMapper
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest
import com.menta.api.feenicia.adapter.rest.model.FeeniciaResponse
import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import com.menta.api.feenicia.adapter.rest.provider.FeeniciaUrlProvider
import com.menta.api.feenicia.application.aCreatedOperation
import com.menta.api.feenicia.application.aFeeniciaRequest
import com.menta.api.feenicia.application.aFeeniciaResponse
import com.menta.api.feenicia.application.aFeeniciaReverseRequest
import com.menta.api.feenicia.application.aPaymentOperation
import com.menta.api.feenicia.application.aReverseOperation
import com.menta.api.feenicia.domain.OperationType.REFUND
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class FeeniciaRestClientSpec : FeatureSpec({

    val toCreatedOperationMapper: ToCreatedOperationMapper = mockk()
    val toFeeniciaRequestMapper: ToFeeniciaRequestMapper = mockk()
    val toFeeniciaRefundRequestMapper: ToFeeniciaRefundRequestMapper = mockk()
    val toFeeniciaReverseRequestMapper: ToFeeniciaReverseRequestMapper = mockk()
    val toRequestHeaderMapper: ToRequestHeaderMapper = mockk()
    val feeniciaUrlProvider: FeeniciaUrlProvider = mockk()
    val mapper: ObjectMapper = mockk()

    feature("Execute web client feenicia") {

        scenario("Successful post feenicia payment") {
            val response = mockk<WebClient.ResponseSpec>(relaxed = true)
            val bodySpec = mockk<WebClient.RequestBodySpec>(relaxed = true)
            val bodyUriSpec = mockk<WebClient.RequestBodyUriSpec>()
            val headerSpec = mockk<WebClient.RequestHeadersSpec<*>>()
            val client = mockk<WebClient>()
            val respAcquirer = aFeeniciaResponse()
            val url = ""
            val aesEncryptionProvider = mockk<AesEncryptionProvider>()
            val operation = aPaymentOperation()

            every { toRequestHeaderMapper.provide(any(), any(), any(), any()) } returns emptyMap()
            every { toFeeniciaRequestMapper.map(any()) } returns aFeeniciaRequest()
            every { feeniciaUrlProvider.provide(any(), any()) } returns ""
            every { client.post() } returns bodyUriSpec
            every { bodySpec.headers(any()) } returns bodySpec
            every { response.bodyToMono(FeeniciaResponse::class.java) } returns Mono.just(respAcquirer)
            every { bodyUriSpec.uri(url) } returns bodySpec
            every { bodySpec.retrieve() } returns response
            every { toCreatedOperationMapper.map(any()) } returns aCreatedOperation()
            every { bodySpec.body(bodySpec, object : ParameterizedTypeReference<FeeniciaRequest>() {}) } returns headerSpec
            every { aesEncryptionProvider.decrypt("", "", operation.feeniciaMerchant.keys.requestSignatureKey) } returns operation.feeniciaMerchant.keys.requestSignatureKey
            every { aesEncryptionProvider.decrypt("", "", operation.feeniciaMerchant.keys.requestSignatureIv) } returns operation.feeniciaMerchant.keys.requestSignatureIv

            FeeniciaRestClient(
                client,
                10000L,
                toCreatedOperationMapper,
                toFeeniciaRequestMapper,
                toFeeniciaRefundRequestMapper,
                toFeeniciaReverseRequestMapper,
                toRequestHeaderMapper,
                feeniciaUrlProvider,
                "",
                "",
                aesEncryptionProvider,
                mapper
            ).execute(operation, null)
        }

        scenario("Successful post feenicia reversal refund") {
            val client = mockk<WebClient>()
            val aesEncryptionProvider = mockk<AesEncryptionProvider>()
            val reverseOperation = aReverseOperation()
            every { toFeeniciaReverseRequestMapper.map(any()) } returns aFeeniciaReverseRequest()
            every { toCreatedOperationMapper.map(any()) } returns aCreatedOperation()
            every { aesEncryptionProvider.decrypt("", "", reverseOperation.feeniciaMerchant.keys.requestSignatureKey) } returns reverseOperation.feeniciaMerchant.keys.requestSignatureKey
            every { aesEncryptionProvider.decrypt("", "", reverseOperation.feeniciaMerchant.keys.requestSignatureIv) } returns reverseOperation.feeniciaMerchant.keys.requestSignatureIv

            FeeniciaRestClient(
                client,
                10000L,
                toCreatedOperationMapper,
                toFeeniciaRequestMapper,
                toFeeniciaRefundRequestMapper,
                toFeeniciaReverseRequestMapper,
                toRequestHeaderMapper,
                feeniciaUrlProvider,
                "",
                "",
                aesEncryptionProvider,
                mapper
            ).execute(reverseOperation, REFUND)
                .shouldBe(aCreatedOperation().right())
        }
    }
})
