package com.kiwi.api.payments.adapter.jpos.provider

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.FieldExtractionError
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component

@Component
class IsoFieldValueExtractor {

    fun extractNullable(isoMsg: ISOMsg, fieldPosition: FieldPosition): String? =
        isoMsg.getValue(fieldPosition.position) as String?

    fun extract(isoMsg: ISOMsg, fieldPosition: FieldPosition): Either<ApplicationError, String> =
        extractNullable(isoMsg, fieldPosition)
            .leftIfNull(FieldExtractionError(fieldPosition))
}

fun <T> T?.leftIfNull(error: ApplicationError) =
    this?.right() ?: error.left()
