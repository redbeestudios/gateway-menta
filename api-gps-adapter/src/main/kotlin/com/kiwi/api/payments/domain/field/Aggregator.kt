package com.kiwi.api.payments.domain.field

import com.kiwi.api.payments.domain.State

data class Aggregator(
    val name: String,
    val commerceCode: String,
    val address: String,
    val childCommerce: ChildCommerce
) {

    data class ChildCommerce(
        val name: String,
        val code: String,
        val state: State,
        val city: String,
        val zip: String,
        val categoryCode: String
    )
}
