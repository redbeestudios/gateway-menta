package com.menta.api.banorte.domain

data class Aggregator(
    val id: String,
    val childCommerce: ChildCommerce
) {

    data class ChildCommerce(
        val id: String,
    )
}
