package com.kiwi.api.reimbursements.hexagonal.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Customer(
    val id: UUID,
    val country: String,
    val legalType: String,
    val businessName: String,
    val fantasyName: String,
    val tax: Tax,
    val activity: String,
    val email: String,
    val phone: String,
    val address: Address,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val settlementCondition: SettlementCondition?,
    val status: String
) {
    data class Tax(
        val type: String,
        val id: String
    )

    data class Address(
        val state: String,
        val city: String,
        val zip: String,
        val street: String,
        val number: String,
        val floor: String?,
        val apartment: String?
    )

    data class BusinessOwner(
        val name: String,
        val surname: String,
        val birthDate: OffsetDateTime,
        val ownerId: OwnerId
    ) {
        data class OwnerId(
            val type: String,
            val number: String
        )
    }

    data class Representative(
        val representativeId: RepresentativeId,
        val birthDate: OffsetDateTime,
        val name: String,
        val surname: String
    ) {
        data class RepresentativeId(
            val type: String,
            val number: String
        )
    }

    data class SettlementCondition(
        val transactionFee: String,
        val settlement: String,
        val cbuOrCvu: String
    )
}
