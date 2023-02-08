package com.kiwi.api.payments.adapter.jpos.builder

import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.adapter.jpos.provider.IsoFieldValueProvider
import com.kiwi.api.payments.adapter.jpos.provider.PingFieldValueProvider
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component

@Component
class IsoMsgBuilder(
    private val isoFieldValueProvider: IsoFieldValueProvider,
    private val pingFieldValueProvider: PingFieldValueProvider
) {

    fun buildPing(): ISOMsg =
        ISOMsg()
            .also { msg ->
                valuesPing().map {
                    msg.set(it.key.position.toString(), it.value)
                }
            }

    private fun valuesPing() =
        pingFieldValueProvider.provide()

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
