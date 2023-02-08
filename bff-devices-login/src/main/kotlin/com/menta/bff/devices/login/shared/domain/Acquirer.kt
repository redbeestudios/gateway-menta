package com.menta.bff.devices.login.shared.domain

data class Acquirer(
    val acquirerId: String,
    val code: String,
    val installments: List<Installment>?,
    val tags: List<String>?
) {
    data class Installment(
        val brand: String,
        val number: String
    )
}
