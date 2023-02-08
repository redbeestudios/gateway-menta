package com.kiwi.api.payments.adapter.jpos

import arrow.core.Either
import arrow.core.filterOrElse
import arrow.core.flatMap
import arrow.core.left
import arrow.core.leftIfNull
import arrow.core.right
import com.kiwi.api.payments.adapter.jpos.builder.IsoMsgBuilder
import com.kiwi.api.payments.adapter.jpos.mapper.ToCreatedOperationMapper
import com.kiwi.api.payments.adapter.jpos.mapper.ToResponsePingMapper
import com.kiwi.api.payments.application.port.out.GlobalClientRepository
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.shared.error.model.AcquirerCommunicationError
import com.kiwi.api.payments.shared.error.model.AcquirerCommunicationTimeout
import com.kiwi.api.payments.shared.error.model.AcquirerPingError
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.exception
import org.jpos.iso.ISOMsg
import org.jpos.iso.MUX
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GlobalProcessingJposClient(
    private val muxPool: MUX,
    private val isoMsgBuilder: IsoMsgBuilder,
    private val toEcoTestMapper: ToResponsePingMapper,
    private val toCreatedOperationMapper: ToCreatedOperationMapper,
    @Value("\${api-gp-adapter.adapter.out.request-timeout}")
    private val requestTimeout: Long
) : GlobalClientRepository {

    override fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation> =
        operation
            .toIsoMsg()
            .send()
            .flatMap { it.asCreatedOnlineOperation() }

    override fun ping(): Either<ApplicationError, Unit> =
        buildIsoMsg()
            .send()
            .fold(
                { AcquirerPingError().left() },
                { it.asResponsePing() }
            )
            .filterOrElse({ it == "00" }, { AcquirerPingError() })
            .void()

    private fun Operation.toIsoMsg() =
        isoMsgBuilder.buildFrom(this)
            .log { info("operation converted to isoMsg") }

    private fun buildIsoMsg() =
        isoMsgBuilder.buildPing()

    private fun ISOMsg.send(): Either<ApplicationError, ISOMsg> =
        try {
            log.info("Ejecutando request: {}", this.toString())
            muxPool.request(this, requestTimeout).right()
                .leftIfNull { AcquirerCommunicationTimeout() }
                .logLeft { info("acquirer communications timeout") }
        } catch (ex: Exception) {
            AcquirerCommunicationError().left()
                .log { exception(ex) }
        }

    private fun ISOMsg.asCreatedOnlineOperation() =
        toCreatedOperationMapper.map(this)

    private fun ISOMsg.asResponsePing() =
        toEcoTestMapper.map(this)

    companion object : CompanionLogger()
}
