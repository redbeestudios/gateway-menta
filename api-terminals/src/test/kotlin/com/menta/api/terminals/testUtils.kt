package com.menta.api.terminals

import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalRequest
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.adapter.`in`.model.TerminalRequest
import com.menta.api.terminals.adapter.`in`.model.TerminalResponse
import com.menta.api.terminals.adapter.`in`.model.hateos.TerminalModel
import com.menta.api.terminals.domain.Feature.CHIP
import com.menta.api.terminals.domain.Feature.CONTACTLESS
import com.menta.api.terminals.domain.Feature.MANUAL
import com.menta.api.terminals.domain.Feature.STRIPE
import com.menta.api.terminals.domain.PreTerminal
import com.menta.api.terminals.domain.Status.ACTIVE
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApiErrorResponse
import com.menta.api.terminals.shared.error.model.ApiErrorResponse.ApiError
import java.time.OffsetDateTime
import java.util.UUID

val aTerminalId = UUID.fromString("09c7395f-41e5-4d5c-8b40-f332894a9c4e")
val aCustomerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab")
const val aSerialCode = "567"
val acquireId = UUID.fromString("542f06cb-6961-4455-a268-9b8e459f23a0")

fun aTerminalRequest() =
    TerminalRequest(
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = aCustomerId,
        serialCode = aSerialCode,
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "zg350",
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )

fun aPreTerminal() =
    PreTerminal(
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
        serialCode = aSerialCode,
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "zg350",
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )

val aTerminalModel = TerminalModel(
    id = aTerminalId,
    merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
    customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
    serialCode = aSerialCode,
    hardwareVersion = "987hgf",
    tradeMark = "pirutchit",
    model = "zg350",
    status = ACTIVE,
    deleteDate = null,
    features = listOf(
        MANUAL,
        STRIPE,
        CHIP,
        CONTACTLESS
    )
)

val aTerminal =
    Terminal(
        id = aTerminalId,
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
        serialCode = aSerialCode,
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "zg350",
        status = ACTIVE,
        deleteDate = null,
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )

val aTerminalDelete =
    Terminal(
        id = aTerminalId,
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
        serialCode = aSerialCode,
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "zg350",
        status = ACTIVE,
        deleteDate = OffsetDateTime.now(),
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )

val aTerminalResponse =
    TerminalResponse(
        id = UUID.fromString("09c7395f-41e5-4d5c-8b40-f332894a9c4e"),
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
        serialCode = "567",
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "zg350",
        status = ACTIVE,
        deleteDate = null,
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )

val anAcquirerTerminalRequest =
    AcquirerTerminalRequest(
        acquirerId = "GPS",
        code = "23456789"
    )

val aPreAcquirerTerminal =
    PreAcquirerTerminal(
        terminalId = aTerminalId,
        acquirerId = "GPS",
        code = "23456789"
    )

val anApiErrorResponse =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiError(
                code = "a code",
                resource = "a resource",
                message = "a message",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )
