package com.kiwi.api.reversal.hexagonal.adapter.out.rest.model

import com.kiwi.api.reversal.hexagonal.domain.Country
import com.kiwi.api.reversal.hexagonal.domain.entities.Feature
import java.time.OffsetDateTime
import java.util.UUID

data class AcquirerRequest(
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val customer: Customer,
    val batch: String,
    val installments: String,
    val retrievalReferenceNumber: String?
) {
    data class Capture(
        val card: Card,
        val inputMode: String,
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder,
            val pan: String?,
            val expirationDate: String?,
            val cvv: String?,
            val track1: String?,
            val track2: String?,
            val pin: String?,
            val emv: EMV?,
            val bank: String?,
            val type: String,
            val brand: String
        ) {
            data class EMV(
                val iccData: String?,
                val cardSequenceNumber: String?,
                val ksn: String?,
            ) {
                override fun toString(): String {
                    return "EMV(cardSequenceNumber=$cardSequenceNumber)"
                }
            }

            data class Holder(
                val name: String,
                val identification: Identification?
            ) {
                data class Identification(
                    val number: String,
                    val type: String
                )
            }

            override fun toString(): String {
                return "Card(emv=$emv, bank=$bank, type='$type', brand='$brand')"
            }
        }
    }

    data class Amount(
        val total: String,
        val currency: String,
        val breakdown: List<Breakdown>
    ) {
        data class Breakdown(
            val description: String,
            val amount: String
        )
    }

    data class Terminal(
        val id: UUID,
        val merchantId: UUID,
        val customerId: UUID,
        val serialCode: String,
        val hardwareVersion: String,
        val softwareVersion: String,
        val tradeMark: String,
        val model: String,
        val status: String,
        val features: List<Feature>
    )

    data class Merchant(
        val id: UUID,
        val customerId: UUID,
        val country: Country,
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
}
