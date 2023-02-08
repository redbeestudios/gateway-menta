package com.menta.api.feenicia.adapter.rest.mapper

import com.menta.api.feenicia.adapter.rest.model.FeeniciaReverseRequest
import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import com.menta.api.feenicia.domain.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ToFeeniciaReverseRequestMapper(
    @Value("\${api-feenicia-adapter.properties.responseIv}")
    private val responseIv: String,
    @Value("\${api-feenicia-adapter.properties.responseKey}")
    private val responseKey: String,
    private val aesEncryptionProvider: AesEncryptionProvider
) {
    fun map(operation: Operation) =
        with(operation) {
            FeeniciaReverseRequest(
                affiliation = affiliation,
                amount = amount,
                cardholderName = aesEncryptionProvider.encrypt(
                    feeniciaMerchant.keys.requestKey.decrypt(responseIv, responseKey),
                    feeniciaMerchant.keys.requestIv.decrypt(responseIv, responseKey),
                    cardholderName
                ),
                track2 = aesEncryptionProvider.encrypt(
                    feeniciaMerchant.keys.requestKey.decrypt(responseIv, responseKey),
                    feeniciaMerchant.keys.requestIv.decrypt(responseIv, responseKey),
                    track2?.replace("d|D".toRegex(), "=")
                ),
                transactionDate = transactionDate
            )
        }

    private fun String.decrypt(requestIv: String, requestKey: String) =
        aesEncryptionProvider.decrypt(requestKey, requestIv, this)
}
