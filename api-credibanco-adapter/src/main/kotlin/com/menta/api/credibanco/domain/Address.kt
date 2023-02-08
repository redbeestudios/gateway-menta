package com.menta.api.credibanco.domain

data class Address(
    val state: String,
    val city: String,
    val zip: String,
    val street: String,
    val number: String,
    val floor: String?,
    val apartment: String?
)
