package com.menta.api.transactions.adapter.`in`.consumer.model

import com.menta.api.transactions.domain.StatusCode
import java.time.OffsetDateTime
import java.util.UUID

data class CreatedRefund(
    val id: String,
    val ticketId: Int?,
    val authorization: Authorization,
    val data: Refund
) {
    data class Authorization(
        val authorizationCode: String?,
        val status: Status,
        val retrievalReferenceNumber: String?,
        val displayMessage: DisplayMessage?
    ) {
        data class DisplayMessage(
            val useCode: String,
            val message: String,
        )

        data class Status(
            val code: StatusCode,
            val situation: Situation?
        ) {
            data class Situation(
                val id: String,
                val description: String
            )
        }
    }

    data class Refund(
        val paymentId: String,
        val merchant: Merchant,
        val terminal: Terminal,
        val capture: Capture,
        val amount: Amount,
        val installments: String,
        val trace: String,
        val ticket: String,
        val batch: String,
        val datetime: OffsetDateTime,
        val customer: Customer,
    ) {
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
            val category: String?,
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
            val features: List<String>
        )

        data class Capture(
            val card: Card,
            val inputMode: String,
            val previousTransactionInputMode: String?,
        ) {
            data class Card(
                val holder: Holder,
                val pan: String?,
                val expirationDate: String?,
                val cvv: String?,
                val track1: String?,
                val track2: String?,
                val iccData: String?,
                val cardSequenceNumber: String?,
                val bank: String?,
                val type: String,
                val brand: String,
                val pin: String?,
                val ksn: String?
            ) {
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
                    return "Card(cardSequenceNumber=$cardSequenceNumber, bank=$bank, type='$type', brand='$brand')"
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
    }
}
