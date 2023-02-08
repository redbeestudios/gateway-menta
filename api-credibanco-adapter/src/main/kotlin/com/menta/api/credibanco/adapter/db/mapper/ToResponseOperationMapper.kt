package com.menta.api.credibanco.adapter.db.mapper

import com.menta.api.credibanco.adapter.controller.provider.IdProvider
import com.menta.api.credibanco.adapter.db.entity.ResponseOperation
import com.menta.api.credibanco.domain.CreatedOperation
import org.springframework.stereotype.Component

@Component
class ToResponseOperationMapper(
    private val idProvider: IdProvider
) {

    fun map(createdOperation: CreatedOperation): ResponseOperation =
        with(createdOperation) {
            ResponseOperation(
                id = idProvider.provide(),
                retrievalReferenceNumber = retrievalReferenceNumber,
                authorizationCode = authorizationCode,
                responseCode = responseCode.code,
                cardTypeResponseCode = cardType,
                receivingInstitutionIdenficationCode = receivingInstitutionIdentificationCode,
                settlementData = settlementDataResponse
            )
        }
}
