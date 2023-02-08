package com.menta.api.feenicia.adapter.rest.mapper

import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import org.springframework.stereotype.Component

@Component
class ToRequestHeaderMapper(
    private val aesEncryptionProvider: AesEncryptionProvider
) {
    fun provide(merchantId: String, signatureKey: String, signatureIV: String, feeniciaRequest: String): Map<String, String?> =
        mapOf(
            "x-requested-with" to "${merchantId}_" + aesEncryptionProvider.encrypt(
                signatureKey,
                signatureIV,
                feeniciaRequest
            )
        )
}
