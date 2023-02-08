package com.menta.api.feenicia.adapter.controller.model

import com.menta.api.feenicia.adapter.controller.mapper.ToOperationMapper
import com.menta.api.feenicia.adapter.controller.provider.ItemsProvider
import com.menta.api.feenicia.application.aFeeniciaMerchant
import com.menta.api.feenicia.application.aPaymentOperation
import com.menta.api.feenicia.application.anOperationRequest
import com.menta.api.feenicia.application.calculateAmount
import com.menta.api.feenicia.domain.EntryMode.CONTACTLESS
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToOperationMapperSpec : FeatureSpec({

    feature("map operation request") {

        val itemsProvider = mockk<ItemsProvider>()

        val mapper = ToOperationMapper(itemsProvider)
        val feeniciaMerchant = aFeeniciaMerchant()

        beforeEach {
            clearAllMocks()
            every { itemsProvider.provide(any()) }.answers { emptyList() }
        }

        scenario("successful mapping with installments") {
            val paymentOperation = aPaymentOperation().copy(contactless = true)
            val operationRequest = anOperationRequest(inputMode = "CONTACTLESS").copy(installments = "03")

            mapper.map(operationRequest, PAYMENT, feeniciaMerchant) shouldBe with(paymentOperation) {
                Operation(
                    affiliation = feeniciaMerchant.affiliation,
                    amount = calculateAmount(),
                    cardholderName = operationRequest.capture.card.holder.name,
                    items = itemsProvider.provide(calculateAmount()),
                    transactionDate = operationRequest.datetime.toInstant().toEpochMilli(),
                    contactless = true,
                    entryMode = CONTACTLESS,
                    emvRequest = operationRequest.capture.card.emv?.iccData,
                    track2 = operationRequest.capture.card.track2,
                    transactionId = operationRequest.retrievalReferenceNumber?.toLongOrNull(),
                    operationType = operationType,
                    deferralData = Operation.DeferralData("03", "00", "03"),
                    orderId = orderId,
                    feeniciaMerchant = feeniciaMerchant
                )
            }
        }

        scenario("successful mapping without installments") {
            val paymentOperation = aPaymentOperation().copy(contactless = true)
            val operationRequest = anOperationRequest(inputMode = "CONTACTLESS").copy(installments = null)

            mapper.map(operationRequest, PAYMENT, feeniciaMerchant) shouldBe with(paymentOperation) {
                Operation(
                    affiliation = feeniciaMerchant.affiliation,
                    amount = calculateAmount(),
                    cardholderName = operationRequest.capture.card.holder.name,
                    items = itemsProvider.provide(calculateAmount()),
                    transactionDate = operationRequest.datetime.toInstant().toEpochMilli(),
                    contactless = true,
                    entryMode = CONTACTLESS,
                    emvRequest = operationRequest.capture.card.emv?.iccData,
                    track2 = operationRequest.capture.card.track2,
                    transactionId = operationRequest.retrievalReferenceNumber?.toLongOrNull(),
                    operationType = operationType,
                    deferralData = null,
                    orderId = orderId,
                    feeniciaMerchant = feeniciaMerchant
                )
            }
        }
    }
})
