package com.menta.api.taxesEntities.domain

data class SettlementCondition(
    val cbuOrCvu: String,
    val accountType: AccountType?
) {
    data class AccountType(
        val id: String,
        val type: String
    )
}
