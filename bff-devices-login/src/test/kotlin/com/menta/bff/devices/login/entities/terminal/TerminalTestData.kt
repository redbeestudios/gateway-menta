package com.menta.bff.devices.login.entities.terminal

import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.entities.terminals.domain.Terminal.Feature.CHIP
import com.menta.bff.devices.login.entities.terminals.domain.Terminal.Feature.CONTACTLESS
import com.menta.bff.devices.login.entities.terminals.domain.Terminal.Feature.MANUAL
import com.menta.bff.devices.login.entities.terminals.domain.Terminal.Feature.STRIPE
import java.util.UUID

fun aTerminal() =
    Terminal(
        id = UUID.fromString("09c7395f-41e5-4d5c-8b40-f332894a9c4e"),
        merchantId = UUID.fromString("e43139b2-6f96-4470-b7e1-1f3b06493549"),
        customerId = UUID.fromString("850363d9-8a9d-4843-acf1-1c1f09be86ab"),
        serialCode = "567",
        hardwareVersion = "987hgf",
        tradeMark = "pirutchit",
        model = "I2000",
        status = "ACTIVE",
        deleteDate = null,
        features = listOf(
            MANUAL,
            STRIPE,
            CHIP,
            CONTACTLESS
        )
    )
