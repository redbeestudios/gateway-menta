package com.menta.bff.devices.login.entities.installments

import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment

fun aAcquirersInstallments() =
    AcquirerInstallment(
        acquirerId = "GPS",
        installments = listOf(
            AcquirerInstallment.Installment(
                brand = "VISA",
                number = "3"
            )
        )
    )
