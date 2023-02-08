package com.menta.api.credibanco.domain

import java.util.UUID

data class Merchant(
    val id: UUID,
    val customerId: UUID,
    val country: Country,
    val legalType: String,
    val businessName: String?,
    val fantasyName: String,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val merchantCode: String,
    val address: Address,
    val email: String,
    val phone: String,
    val activity: String,
    val category: String,
    val tax: Tax,
    val settlementCondition: SettlementCondition
) {
    data class Representative(
        val id: RepresentativeId,
        val birthDate: String,
        val name: String,
        val surname: String
    ) {
        data class RepresentativeId(
            val type: String,
            val number: String
        )
    }

    data class BusinessOwner(
        val name: String,
        val surname: String,
        val birthDate: String,
        val id: OwnerId
    ) {
        data class OwnerId(
            val type: String,
            val number: String
        )
    }

    data class SettlementCondition(
        val transactionFee: String,
        val settlement: String,
        val cbuOrCvu: String
    )

    data class Tax(
        val id: String,
        val type: String

    )
}
