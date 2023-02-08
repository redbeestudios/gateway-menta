package com.menta.api.merchants.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.UUID

@Document(collection = "merchants")
data class Merchant(
    @Id
    val id: UUID,
    val customerId: UUID,
    val country: Country,
    val legalType: LegalType,
    val businessName: String,
    val fantasyName: String,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val merchantCode: String,
    val address: Address,
    val email: String,
    val phone: String,
    val activity: String,
    val category: String,
    val status: Status,
    val tax: Tax,
    val settlementCondition: SettlementCondition,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime?,
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
        val state: State,
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
