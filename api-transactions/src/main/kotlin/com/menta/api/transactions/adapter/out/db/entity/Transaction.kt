package com.menta.api.transactions.adapter.out.db.entity

import org.hibernate.annotations.Type
import java.time.OffsetDateTime
import java.util.Objects
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "transactions")
open class Transaction(
    @Id
    @Column(name = "transaction_id")
    @Type(type = "uuid-char")
    open val transactionId: UUID,
    open val type: String,
    @Type(type = "uuid-char")
    open val merchantId: UUID,
    @Type(type = "uuid-char")
    open val customerId: UUID,
    @Type(type = "uuid-char")
    open val terminalId: UUID,
    open val originalAmount: String?,
    open val refundedAmount: String?,
    open val currency: String,
    open val installmentsNumber: String,
    open val installmentsPlan: String,
    open val cardType: String?,
    open val cardMask: String?,
    open val holderName: String?,
    open val holderDocument: String?,
    open val cardBrand: String?,
    open val cardBank: String?,
    open val createdDatetime: OffsetDateTime,
    open val serialCode: String
) {
    override fun equals(other: Any?): Boolean =
        (other is Transaction) &&
            transactionId == other.transactionId &&
            type == other.type &&
            merchantId == other.merchantId &&
            customerId == other.customerId &&
            terminalId == other.terminalId &&
            originalAmount == other.originalAmount &&
            refundedAmount == other.refundedAmount &&
            currency == other.currency &&
            installmentsNumber == other.installmentsNumber &&
            installmentsPlan == other.installmentsPlan &&
            cardType == other.cardType &&
            cardMask == other.cardMask &&
            holderName == other.holderName &&
            holderDocument == other.holderDocument &&
            cardBrand == other.cardBrand &&
            cardBank == other.cardBank &&
            createdDatetime == other.createdDatetime &&
            serialCode == other.serialCode

    override fun toString() =
        "Transaction( " +
            "transactionId: $transactionId, " +
            "type: $type, " +
            "merchantId: $merchantId, " +
            "customerId: $customerId, " +
            "terminalId: $terminalId, " +
            "originalAmount: $originalAmount, " +
            "refundedAmount: $refundedAmount, " +
            "currency: $currency, " +
            "installmentsNumber: $installmentsNumber, " +
            "installmentsPlan: $installmentsPlan, " +
            "cardType: $cardType, " +
            "cardMask: $cardMask, " +
            "holderName: $holderName, " +
            "holderDocument: $holderDocument" +
            "cardBrand: $cardBrand, " +
            "cardBank: $cardBank, " +
            "createdDatetime: $createdDatetime)"

    override fun hashCode(): Int {
        return Objects.hash(transactionId)
    }
}
