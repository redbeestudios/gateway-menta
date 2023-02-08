package com.menta.api.customers.customer.adapter.`in`.model

import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.LegalType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

data class CustomerRequest(
    @Schema(type = "string", example = "ARG", description = "a country")
    val country: Country,
    @Schema(type = "string", example = "NATURAL_PERSON", description = "a legal type")
    val legalType: LegalType,
    @Schema(type = "string", example = "KIWI", description = "a business name")
    val businessName: String,
    @Schema(type = "string", example = "MENTA", description = "a fantasy name")
    val fantasyName: String,
    val tax: Tax,
    @Schema(type = "string", example = "MAYORISTA", description = "an activity")
    val activity: String,
    val address: Address,
    @Schema(type = "string", example = "example@example.com", description = "an email")
    val email: String,
    @Schema(type = "string", example = "1122334455", description = "a phone")
    val phone: String,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val settlementCondition: SettlementCondition?,
) {
    data class Tax(
        @Schema(type = "string", example = "Responsable Inscripto", description = "a tax type")
        val type: String,
        @Schema(type = "string", example = "30716155877", description = "a tax id")
        val id: String
    )

    data class Address(
        @Schema(type = "string", example = "Argentina", description = "a state")
        val state: String,
        @Schema(type = "string", example = "Bs As", description = "a city")
        val city: String,
        @Schema(type = "string", example = "6000", description = "a zip code")
        val zip: String,
        @Schema(type = "string", example = "Miguel Calixto", description = "a street")
        val street: String,
        @Schema(type = "string", example = "123", description = "a street number")
        val number: String,
        @Schema(type = "string", example = "1", description = "a floor")
        val floor: String?,
        @Schema(type = "string", example = "A", description = "a apartment")
        val apartment: String?
    )

    data class BusinessOwner(
        @Schema(type = "string", example = "Juan", description = "a business owner name")
        val name: String,
        @Schema(type = "string", example = "Perez", description = "a business owner name")
        val surname: String,
        @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a business owner birthdate")
        val birthDate: OffsetDateTime,
        val ownerId: OwnerId
    ) {
        data class OwnerId(
            @Schema(type = "string", example = "DNI", description = "a ownerId type")
            val type: String,
            @Schema(type = "string", example = "33220658", description = "a ownerId number")
            val number: String
        )
    }

    data class Representative(
        val representativeId: RepresentativeId,
        @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a representative birthdate")
        val birthDate: OffsetDateTime,
        @Schema(type = "string", example = "Juan", description = "a representative name")
        val name: String,
        @Schema(type = "string", example = "Perez", description = "a representative surname")
        val surname: String
    ) {
        data class RepresentativeId(
            @Schema(type = "string", example = "DNI", description = "a representative type")
            val type: String,
            @Schema(type = "string", example = "33220658", description = "a representative number")
            val number: String
        )
    }

    data class SettlementCondition(
        @Schema(type = "string", example = "1", description = "a transaction fee")
        val transactionFee: String,
        @Schema(type = "string", example = "1", description = "a settlement")
        val settlement: String,
        @Schema(type = "string", example = "12345678901234567890", description = "a cbu or cvu")
        val cbuOrCvu: String
    )
}
