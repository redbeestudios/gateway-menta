package com.menta.bff.devices.login.entities.installments.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class AcquirerInstallment(
    val acquirerId: String,
    @JsonProperty("data")
    val installments: List<Installment>
) {
    data class Installment(
        val brand: String,
        val number: String
    )
}
