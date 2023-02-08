package com.menta.api.feenicia.adapter.controller.model

import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaRequestMapper
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest
import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import com.menta.api.feenicia.application.aPaymentOperation
import com.menta.api.feenicia.application.calculateAmount
import com.menta.api.feenicia.domain.Operation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToFeeniciaRequestMapperSpec : FeatureSpec({

    feature("map operation request") {

        val aesEncryptionProvider = mockk<AesEncryptionProvider>()

        val mapper = ToFeeniciaRequestMapper("", "", aesEncryptionProvider)

        beforeEach {
            clearAllMocks()
            every { aesEncryptionProvider.encrypt(any(), any(), any()) }.answers { "" }
        }

        scenario("successful mapping") {
            val paymentOperation = aPaymentOperation().copy(contactless = true)
            every { aesEncryptionProvider.decrypt("", "", paymentOperation.feeniciaMerchant.keys.requestIv) } returns paymentOperation.feeniciaMerchant.keys.requestIv
            every { aesEncryptionProvider.decrypt("", "", paymentOperation.feeniciaMerchant.keys.requestKey) } returns paymentOperation.feeniciaMerchant.keys.requestKey
                .toString()
            mapper.map(paymentOperation) shouldBe with(paymentOperation) {
                FeeniciaRequest(
                    affiliation = affiliation,
                    amount = amount,
                    cardholderName = aesEncryptionProvider.encrypt(
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestKey),
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestIv),
                        cardholderName
                    ),
                    items = listOf(
                        FeeniciaRequest.Items(
                            id = 0,
                            amount = calculateAmount(),
                            description = "",
                            quantity = 1,
                            unitPrice = calculateAmount().toString()
                        )
                    ),
                    userId = feeniciaMerchant.userId,
                    transactionDate = transactionDate,
                    contactless = contactless,
                    emvRequest = emvRequest,
                    terminal = terminal,
                    tip = tip,
                    track2 = aesEncryptionProvider.encrypt(
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestKey),
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestIv),
                        track2?.replace("d|D".toRegex(), "=")
                    ),
                    geoData = null,
                    deferralData = null
                )
            }
        }

        scenario("successful mapping with installments") {
            val paymentOperation =
                aPaymentOperation().copy(contactless = true, deferralData = Operation.DeferralData(payments = "03"))

            every { aesEncryptionProvider.decrypt("", "", paymentOperation.feeniciaMerchant.keys.requestIv) } returns paymentOperation.feeniciaMerchant.keys.requestIv
            every { aesEncryptionProvider.decrypt("", "", paymentOperation.feeniciaMerchant.keys.requestKey) } returns paymentOperation.feeniciaMerchant.keys.requestKey

            mapper.map(paymentOperation) shouldBe with(paymentOperation) {
                FeeniciaRequest(
                    affiliation = affiliation,
                    amount = amount,
                    cardholderName = aesEncryptionProvider.encrypt(
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestKey),
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestIv),
                        cardholderName
                    ),
                    items = listOf(
                        FeeniciaRequest.Items(
                            id = 0,
                            amount = calculateAmount(),
                            description = "",
                            quantity = 1,
                            unitPrice = calculateAmount().toString()
                        )
                    ),
                    userId = feeniciaMerchant.userId,
                    transactionDate = transactionDate,
                    contactless = contactless,
                    emvRequest = emvRequest,
                    terminal = terminal,
                    tip = tip,
                    track2 = aesEncryptionProvider.encrypt(
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestKey),
                        aesEncryptionProvider.decrypt("", "", feeniciaMerchant.keys.requestIv),
                        track2?.replace("d|D".toRegex(), "=")
                    ),
                    geoData = null,
                    deferralData = deferralData?.let {
                        FeeniciaRequest.DeferralData(
                            it.planType,
                            it.deferral,
                            it.payments
                        )
                    }
                )
            }
        }
    }
})
