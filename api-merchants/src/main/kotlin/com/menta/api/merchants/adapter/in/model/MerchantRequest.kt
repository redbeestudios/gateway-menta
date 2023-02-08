package com.menta.api.merchants.adapter.`in`.model

import com.menta.api.merchants.domain.Country
import com.menta.api.merchants.domain.LegalType
import com.menta.api.merchants.domain.State
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

data class MerchantRequest(
    val customerId: UUID,
    @Schema(example = "ARG", description = "a country")
    val country: Country,
    @Schema(example = "NATURAL_PERSON", description = "a legal type")
    val legalType: LegalType,
    @Schema(example = "kiwi", description = "a business name")
    val businessName: String,
    @Schema(example = "menta", description = "a fantasy name")
    val fantasyName: String,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    @Schema(example = "menta", description = "a merchant code")
    val merchantCode: String,
    val address: Address,
    @Schema(type = "string", example = "johnDoe@menta.com", description = "an email")
    val email: String,
    @Schema(type = "string", example = "+541199999999", description = "a phone number")
    val phone: String,
    @Schema(type = "string", example = "MAYORISTA", description = "an activity")
    val activity: String,
    @Schema(type = "string", example = "?", description = "a category")
    val category: String,
    val tax: Tax,
    val settlementCondition: SettlementCondition

) {
    data class Representative(
        val representativeId: RepresentativeId,
        @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a representative birthdate")
        val birthDate: OffsetDateTime,
        @Schema(type = "string", example = "John", description = "a representative name")
        val name: String,
        @Schema(type = "string", example = "Doe", description = "a representative surname")
        val surname: String
    ) {
        data class RepresentativeId(
            @Schema(type = "string", example = "DNI", description = "a representativeId type")
            val type: String,
            @Schema(type = "string", example = "33220658", description = "a representativeId number")
            val number: String
        )
    }

    data class BusinessOwner(
        @Schema(type = "string", example = "John", description = "a business owner name")
        val name: String,
        @Schema(type = "string", example = "Doe", description = "a business owner name")
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

    data class SettlementCondition(
        @Schema(type = "string", example = "1", description = "a transaction fee")
        val transactionFee: String,
        @Schema(type = "string", example = "1", description = "a settlement")
        val settlement: String,
        @Schema(type = "string", example = "2850590940090418135201", description = "an uniform bank or virtual code")
        val cbuOrCvu: String
    )

    data class Address(
        @Schema(type = "string", example = "BUENOS_AIRES", description = "a state")
        val state: State,
        @Schema(type = "string", example = "Capital Federal", description = "a city")
        val city: String,
        @Schema(type = "string", example = "C1045AAO", description = "a zip code")
        val zip: String,
        @Schema(type = "string", example = "Av. Corrientes", description = "a street")
        val street: String,
        @Schema(type = "string", example = "2000", description = "a street number")
        val number: String,
        @Schema(type = "string", example = "1", description = "a floor")
        val floor: String?,
        @Schema(type = "string", example = "A", description = "a apartment")
        val apartment: String?
    )

    data class Tax(
        @Schema(type = "string", example = "30-99999999-3", description = "a tax number id")
        val id: String,
        @Schema(type = "string", example = "RESPONSABLE_INSCRIPTO", description = "a tax type")
        val type: String
    )
}
