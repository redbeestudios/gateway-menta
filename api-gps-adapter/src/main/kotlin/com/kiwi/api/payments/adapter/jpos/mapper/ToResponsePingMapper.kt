package com.kiwi.api.payments.adapter.jpos.mapper

import arrow.core.Either
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.RESPONSE_CODE
import com.kiwi.api.payments.adapter.jpos.provider.IsoFieldValueExtractor
import com.kiwi.api.payments.shared.error.model.ApplicationError
import kotlinx.coroutines.runBlocking
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component

@Component
class ToResponsePingMapper(private val isoFieldValueExtractor: IsoFieldValueExtractor) {

    fun map(isoMsg: ISOMsg): Either<ApplicationError, String> =
        runBlocking {
            isoMsg.getValue(RESPONSE_CODE)
        }

    private fun ISOMsg.getValue(field: FieldPosition) =
        isoFieldValueExtractor.extract(this, field)
}
