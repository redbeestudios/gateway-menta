package com.menta.api.feenicia.adapter.rest.mapper

import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest.DeferralData
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest.GeoData
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest.Items
import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import com.menta.api.feenicia.domain.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ToFeeniciaRequestMapper(
    @Value("\${api-feenicia-adapter.properties.responseIv}")
    private val responseIv: String,
    @Value("\${api-feenicia-adapter.properties.responseKey}")
    private val responseKey: String,
    private val aesEncryptionProvider: AesEncryptionProvider
) {

    companion object {
        private const val ONE_PAYMENT = "01"
    }
    fun map(operation: Operation) =
        with(operation) {
            FeeniciaRequest(
                affiliation = affiliation,
                amount = amount,
                cardholderName = aesEncryptionProvider.encrypt(
                    feeniciaMerchant.keys.requestKey.decrypt(responseIv, responseKey),
                    feeniciaMerchant.keys.requestIv.decrypt(responseIv, responseKey),
                    cardholderName
                ),
                items = items.map { buildItems(it) },
                userId = feeniciaMerchant.userId,
                transactionDate = transactionDate,
                contactless = contactless,
                emvRequest = emvRequest,
                terminal = terminal,
                tip = tip,
                track2 = aesEncryptionProvider.encrypt(
                    feeniciaMerchant.keys.requestKey.decrypt(responseIv, responseKey),
                    feeniciaMerchant.keys.requestIv.decrypt(responseIv, responseKey),
                    track2?.replace("d|D".toRegex(), "=")
                ),
                geoData = geoData?.let { buildGeoData(it) },
                deferralData = deferralData?.let { buildDeferralData(it) }
            )
        }

    private fun buildDeferralData(deferralData: Operation.DeferralData) =
        with(deferralData) {
            if (payments != ONE_PAYMENT)
                DeferralData(planType = planType, deferral = deferral, payments = payments)
            else
                null
        }

    private fun buildGeoData(geoData: Operation.GeoData) =
        with(geoData) {
            GeoData(
                latitude = latitude,
                longitude = longitude
            )
        }

    private fun buildItems(items: Operation.Items) =
        with(items) {
            Items(
                amount = amount,
                description = description,
                id = id,
                quantity = quantity,
                unitPrice = unitPrice
            )
        }

    private fun String.decrypt(requestIv: String, requestKey: String) =
        aesEncryptionProvider.decrypt(requestKey, requestIv, this)
}
