package com.menta.api.banorte.domain

data class BanorteMerchant(
    val id: String,
    val affiliationId: String,
    val user: User,
    val url: String
) {
    data class User(
        val username: String,
        val password: String
    )
}
