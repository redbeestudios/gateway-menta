package com.menta.api.feenicia.adapter.controller.rest

import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class AesEncryptionProviderSpec : FeatureSpec({

    feature("Test encryption methods") {

        scenario("encrypt data when is ok") {
            val provider = AesEncryptionProvider("ISO-8859-1", "AES/CBC/PKCS7Padding")

            provider.encrypt(
                "96b02bafc6f822450b3aa3bf8adf2664",
                "e3a22c72a10cf48e8f85f934e18377d0",
                "TEST EMV"
            ).shouldBe("57aff7fbd0c049480e50ac15947dcc90")
        }

        scenario("decrypt data when is ok") {
            val provider = AesEncryptionProvider("ISO-8859-1", "AES/CBC/PKCS7Padding")

            provider.decrypt(
                "8899c6e38f5bd297727c1c07eda3b613",
                "6baa014c4508f16a57fb1f779a95a557",
                "83db69958be8c61fd18d6f209d326902e4c0d991686f889a11540787929fe62643f854adf7aa359dc2be8c99994dd7fb"
            ).shouldBe("7d16efad0afbc66d678b181dd2bf2190")
        }
    }
})
