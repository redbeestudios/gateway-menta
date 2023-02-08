package com.menta.api.credibanco.adapter.jpos.builder

import com.menta.api.credibanco.adapter.jpos.models.FieldPosition
import com.menta.api.credibanco.adapter.jpos.provider.IsoFieldValueProvider
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component

@Component
class IsoMsgBuilder(
    private val isoFieldValueProvider: IsoFieldValueProvider
) {

    fun buildFrom(operation: Operation): ISOMsg =
        ISOMsg()
            .also { msg ->
                FieldPosition.values()
                    .map { msg.addField(it, operation) }
            }
            .log { info("isoMsg built") }

    private fun ISOMsg.addField(fieldPosition: FieldPosition, operation: Operation) =
        getValueFrom(fieldPosition, operation)
            ?.also { set(fieldPosition.position, it) }

    private fun getValueFrom(position: FieldPosition, operation: Operation) =
        isoFieldValueProvider.provide(position, operation)
            .log { debug("value {} extracted from {}", it, position.position) }

    companion object : CompanionLogger()
}
