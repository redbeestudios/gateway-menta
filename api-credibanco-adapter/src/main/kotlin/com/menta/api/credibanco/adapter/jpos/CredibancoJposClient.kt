package com.menta.api.credibanco.adapter.jpos

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.leftIfNull
import arrow.core.right
import com.menta.api.credibanco.adapter.jpos.builder.IsoMsgBuilder
import com.menta.api.credibanco.adapter.jpos.mapper.ToCreatedOperationMapper
import com.menta.api.credibanco.application.port.out.CredibancoRepository
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.shared.error.model.AcquirerCommunicationError
import com.menta.api.credibanco.shared.error.model.AcquirerCommunicationTimeout
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import com.menta.api.credibanco.shared.util.log.exception
import org.jpos.iso.ISOMsg
import org.jpos.iso.MUX
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CredibancoJposClient(
    private val muxPool: MUX,
    private val toCreatedOperationMapper: ToCreatedOperationMapper,
    private val isoMsgBuilder: IsoMsgBuilder,
    @Value("\${api-credibanco-adapter.adapter.out.request-timeout}")
    private val requestTimeout: Long
) : CredibancoRepository {

    override fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation> =
        operation
            .toIsoMsg()
            .send()
            .flatMap { it.asCreatedOnlineOperation() }

    private fun Operation.toIsoMsg() =
        isoMsgBuilder.buildFrom(this)

    private fun ISOMsg.send(): Either<ApplicationError, ISOMsg> =
        try {
            log.info("Ejecutando request credibanco")
            muxPool.request(this, requestTimeout).right()
                .leftIfNull { AcquirerCommunicationTimeout() }
                .logLeft { info("acquirer communications timeout") }
        } catch (ex: Exception) {
            AcquirerCommunicationError().left()
                .log { exception(ex) }
        }
    private fun ISOMsg.asCreatedOnlineOperation() =
        toCreatedOperationMapper.map(this)

    companion object : CompanionLogger()
}
