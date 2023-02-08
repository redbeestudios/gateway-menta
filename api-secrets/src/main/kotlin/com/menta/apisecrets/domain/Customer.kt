package com.menta.apisecrets.domain

import java.util.UUID

data class Customer(
    val id: UUID,
    val country: Country
)
