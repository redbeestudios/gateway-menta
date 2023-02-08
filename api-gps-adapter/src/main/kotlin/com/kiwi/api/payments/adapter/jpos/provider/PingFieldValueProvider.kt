package com.kiwi.api.payments.adapter.jpos.provider

import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AUDIT_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.NETWORK_INTERNATIONAL_IDENTIFIER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TRANSMISSION_DATE_TIME
import com.kiwi.api.payments.domain.field.MTI
import com.kiwi.api.payments.domain.field.provider.ConstantsProvider
import com.kiwi.api.payments.extensions.toAcquirerFormat
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class PingFieldValueProvider(
    private val constantsProvider: ConstantsProvider
) {
    fun provide() =
        mapOf<FieldPosition, String>(
            FieldPosition.MTI to MTI.PING.code,
            TRANSMISSION_DATE_TIME to OffsetDateTime.now().toAcquirerFormat(),
            AUDIT_NUMBER to constantsProvider.provideAuditNumber(),
            NETWORK_INTERNATIONAL_IDENTIFIER to constantsProvider.provideNII(),
        )
}
