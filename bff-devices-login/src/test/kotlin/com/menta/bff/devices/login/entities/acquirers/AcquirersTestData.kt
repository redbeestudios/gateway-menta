package com.menta.bff.devices.login.entities.acquirers

import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.shared.domain.Acquirer

fun aAcquirersOperable() =
    listOf(
        AcquirerOperable(
            acquirerId = "GPS",
            code = "a code"
        )
    )

fun aAcquirers() =
    listOf(
        Acquirer(
            acquirerId = "GPS",
            code = "a code",
            installments = listOf(
                Acquirer.Installment(
                    brand = "VISA",
                    number = "3"
                )
            ),
            tags = listOf("9F26", "82", "9F36")
        )
    )
