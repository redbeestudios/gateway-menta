package com.menta.api.merchants.adapter.`in`.model

import com.menta.api.merchants.domain.Country
import com.menta.api.merchants.domain.LegalType
import com.menta.api.merchants.domain.State
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

data class UpdateRequest(
    val customerId: UUID? = null,    @Schema(example = "ARG", description = "a country")
    val country: Country? = null,
    @Schema(example = "NATURAL_PERSON", description = "a legal type")
    val legalType: LegalType? = null,
    @Schema(example = "kiwi", description = "a business name")
    val businessName: String? = null,
    @Schema(example = "menta", description = "a fantasy name")
    val fantasyName: String? = null,
    val representative: Representative? = null,
    val businessOwner: BusinessOwner? = null,
    @Schema(example = "menta", description = "a merchant code")
    val merchantCode: String? = null,
    val address: Address? = null,
    @Schema(type = "string", example = "johnDoe@menta.com", description = "an email")
    val email: String? = null,
    @Schema(type = "string", example = "+541199999999", description = "a phone number")
    val phone: String? = null,
    @Schema(type = "string", example = "MAYORISTA", description = "an activity")
    val activity: String? = null,
    @Schema(type = "string", example = "?", description = "a category")
    val category: String? = null,
    val tax: Tax? = null,
    val settlementCondition: SettlementCondition? = null

) {
    data class Representative(
        val representativeId: RepresentativeId? = null,
        @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a representative birthdate")
        val birthDate: OffsetDateTime? = null,
        @Schema(type = "string", example = "John", description = "a representative name")
        val name: String? = null,
        @Schema(type = "string", example = "Doe", description = "a representative surname")
        val surname: String? = null
    ) {
        data class RepresentativeId(
            @Schema(type = "string", example = "DNI", description = "a representativeId type")
            val type: String? = null,
            @Schema(type = "string", example = "33220658", description = "a representativeId number")
            val number: String? = null
        )
    }

    data class BusinessOwner(
        @Schema(type = "string", example = "John", description = "a business owner name")
        val name: String? = null,
        @Schema(type = "string", example = "Doe", description = "a business owner name")
        val surname: String? = null,
        @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a business owner birthdate")
        val birthDate: OffsetDateTime? = null,
        val ownerId: OwnerId? = null
    ) {
        data class OwnerId(
            @Schema(type = "string", example = "DNI", description = "a ownerId type")
            val type: String? = null,
            @Schema(type = "string", example = "33220658", description = "a ownerId number")
            val number: String? = null
        )
    }

    data class SettlementCondition(
        @Schema(type = "string", example = "1", description = "a transaction fee")
        val transactionFee: String? = null,
        @Schema(type = "string", example = "1", description = "a settlement")
        val settlement: String? = null,
        @Schema(type = "string", example = "2850590940090418135201", description = "an uniform bank or virtual code")
        val cbuOrCvu: String? = null
    )

    data class Address(
        @Schema(type = "string", example = "BUENOS_AIRES", description = "a state")
        val state: State? = null,
        @Schema(type = "string", example = "Capital Federal", description = "a city")
        val city: String? = null,
        @Schema(type = "string", example = "C1045AAO", description = "a zip code")
        val zip: String? = null,
        @Schema(type = "string", example = "Av. Corrientes", description = "a street")
        val street: String? = null,
        @Schema(type = "string", example = "2000", description = "a street number")
        val number: String? = null,
        @Schema(type = "string", example = "1", description = "a floor")
        val floor: String? = null,
        @Schema(type = "string", example = "A", description = "a apartment")
        val apartment: String? = null
    )

    data class Tax(
        @Schema(type = "string", example = "30-99999999-3", description = "a tax number id")
        val id: String? = null,
        @Schema(type = "string", example = "RESPONSABLE_INSCRIPTO", description = "a tax type")
        val type: String? = null
    )
}
