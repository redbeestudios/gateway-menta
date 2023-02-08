package com.menta.bff.devices.login.entities.merchant.domain

import com.menta.bff.devices.login.entities.merchant.domain.taxes.FiscalCondition
import java.time.OffsetDateTime
import java.util.UUID

data class Merchant(
    val id: UUID,
    val customerId: UUID,
    val country: String,
    val legalType: String,
    val businessName: String?,
    val fantasyName: String?,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val merchantCode: String,
    val address: Address,
    val email: String,
    val phone: String,
    val activity: String,
    val category: String,
    val taxCondition: FiscalCondition?,
    val tax: Tax,
    val settlementCondition: SettlementCondition,
    val deleteDate: OffsetDateTime?
) {
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

    data class SettlementCondition(
        val transactionFee: String,
        val settlement: String,
        val cbuOrCvu: String
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

    data class Tax(
        val id: String,
        val type: String
    )
}
