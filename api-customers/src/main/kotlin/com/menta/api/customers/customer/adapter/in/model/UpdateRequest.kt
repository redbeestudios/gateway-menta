package com.menta.api.customers.customer.adapter.`in`.model

import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.LegalType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

data class UpdateRequest(
    @Schema(type = "string", example = "ARG", description = "a country")
    val country: Country? = null,
    @Schema(type = "string", example = "NATURAL_PERSON", description = "a legal type")
    val legalType: LegalType? = null,
    @Schema(type = "string", example = "KIWI", description = "a business name")
    val businessName: String? = null,
    @Schema(type = "string", example = "MENTA", description = "a fantasy name")
    val fantasyName: String? = null,
    val tax: Tax? = null,
    @Schema(type = "string", example = "MAYORISTA", description = "an activity")
    val activity: String? = null,
    val address: Address? = null,
    @Schema(type = "string", example = "example@example.com", description = "an email")
    val email: String? = null,
    @Schema(type = "string", example = "1122334455", description = "a phone")
    val phone: String? = null,
    val representative: Representative? = null,
    val businessOwner: BusinessOwner? = null,
    val settlementCondition: SettlementCondition? = null,
    ) {
        data class Tax(
            @Schema(type = "string", example = "Responsable Inscripto", description = "a tax type")
            val type: String? = null,
            @Schema(type = "string", example = "30716155877", description = "a tax id")
            val id: String? = null
        )

        data class Address(
            @Schema(type = "string", example = "Argentina", description = "a state")
            val state: String? = null,
            @Schema(type = "string", example = "Bs As", description = "a city")
            val city: String? = null,
            @Schema(type = "string", example = "6000", description = "a zip code")
            val zip: String? = null,
            @Schema(type = "string", example = "Miguel Calixto", description = "a street")
            val street: String? = null,
            @Schema(type = "string", example = "123", description = "a street number")
            val number: String? = null,
            @Schema(type = "string", example = "1", description = "a floor")
            val floor: String? = null,
            @Schema(type = "string", example = "A", description = "a apartment")
            val apartment: String? = null
        )

        data class BusinessOwner(
            @Schema(type = "string", example = "Juan", description = "a business owner name")
            val name: String? = null,
            @Schema(type = "string", example = "Perez", description = "a business owner name")
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

        data class Representative(
            val representativeId: RepresentativeId?,
            @Schema(type = "string", example = "2022-06-03T03:00:00.000Z", description = "a representative birthdate")
            val birthDate: OffsetDateTime? = null,
            @Schema(type = "string", example = "Juan", description = "a representative name")
            val name: String? = null,
            @Schema(type = "string", example = "Perez", description = "a representative surname")
            val surname: String? = null
        ) {
            data class RepresentativeId(
                @Schema(type = "string", example = "DNI", description = "a representative type")
                val type: String? = null,
                @Schema(type = "string", example = "33220658", description = "a representative number")
                val number: String? = null
            )
        }

        data class SettlementCondition(
            @Schema(type = "string", example = "1", description = "a transaction fee")
            val transactionFee: String? = null,
            @Schema(type = "string", example = "1", description = "a settlement")
            val settlement: String? = null,
            @Schema(type = "string", example = "12345678901234567890", description = "a cbu or cvu")
            val cbuOrCvu: String? = null
        )
    }
