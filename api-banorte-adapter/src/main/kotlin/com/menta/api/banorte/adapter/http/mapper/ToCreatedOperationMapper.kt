package com.menta.api.banorte.adapter.http.mapper

import arrow.core.Either
import arrow.core.computations.either
import com.menta.api.banorte.adapter.http.model.HeaderResponse
import com.menta.api.banorte.adapter.http.model.HeaderResponse.AUTH_CODE
import com.menta.api.banorte.adapter.http.model.HeaderResponse.AUTH_RESULT
import com.menta.api.banorte.adapter.http.model.HeaderResponse.CARD_BRAND
import com.menta.api.banorte.adapter.http.model.HeaderResponse.CARD_TYPE
import com.menta.api.banorte.adapter.http.model.HeaderResponse.CONTROL_NUMBER
import com.menta.api.banorte.adapter.http.model.HeaderResponse.CUST_REQ_DATE
import com.menta.api.banorte.adapter.http.model.HeaderResponse.EMV_DATA
import com.menta.api.banorte.adapter.http.model.HeaderResponse.ISSUING_BANK
import com.menta.api.banorte.adapter.http.model.HeaderResponse.MERCHANT_ID
import com.menta.api.banorte.adapter.http.model.HeaderResponse.PAYW_CODE
import com.menta.api.banorte.adapter.http.model.HeaderResponse.PAYW_RESULT
import com.menta.api.banorte.adapter.http.model.HeaderResponse.REFERENCE
import com.menta.api.banorte.adapter.http.model.HeaderResponse.REFERRED_CARD
import com.menta.api.banorte.adapter.http.model.HeaderResponse.TEXT
import com.menta.api.banorte.adapter.http.provider.ResponseProvider
import com.menta.api.banorte.domain.AuthResult
import com.menta.api.banorte.domain.CardBrand
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.CreatedOperation.Card
import com.menta.api.banorte.domain.CreatedOperation.PawsResult
import com.menta.api.banorte.extensions.toOffsetDateTime
import com.menta.api.banorte.shared.error.model.ApplicationError
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class ToCreatedOperationMapper(
    private val responseProvider: ResponseProvider
) {

    fun map(httpHeaders: HttpHeaders): Either<ApplicationError, CreatedOperation> =
        runBlocking {
            either {
                CreatedOperation(
                    card = Card(
                        bank = httpHeaders.getValue(ISSUING_BANK).bind(),
                        brand = httpHeaders.getValue(CARD_BRAND).bind().let {
                            CardBrand.valueOf(it)
                        },
                        type = httpHeaders.getValue(CARD_TYPE).bind(),
                        referredPan = httpHeaders.getValueOptional(REFERRED_CARD)
                    ),
                    authorizationCode = httpHeaders.getValue(AUTH_CODE).bind(),
                    authResult = httpHeaders.getValue(AUTH_RESULT).bind().let {
                        AuthResult.from(it)
                    }.bind(),
                    controlNumber = httpHeaders.getValue(CONTROL_NUMBER).bind(),
                    affiliationId = httpHeaders.getValue(MERCHANT_ID).bind(),
                    paywCode = httpHeaders.getValueOptional(PAYW_CODE),
                    paywResult = httpHeaders.getValue(PAYW_RESULT).bind().let {
                        PawsResult.valueOf(it)
                    },
                    referenceNumber = httpHeaders.getValue(REFERENCE).bind(),
                    text = httpHeaders.getValue(TEXT).bind(),
                    custReqDate = httpHeaders.getValue(CUST_REQ_DATE).bind().toOffsetDateTime(),
                    emvData = httpHeaders.getValue(EMV_DATA).bind(),
                )
            }
        }

    fun HttpHeaders.getValue(header: HeaderResponse) =
        responseProvider.extract(this, header)

    fun HttpHeaders.getValueOptional(header: HeaderResponse) =
        responseProvider.extractNullable(this, header)
}
