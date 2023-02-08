package com.menta.api.taxed.operations.adapter.out.db.entity

import org.hibernate.annotations.Type
import java.time.OffsetDateTime
import java.util.Objects
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "taxed_operation")
data class TaxedOperation(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "payment_id")
    @Type(type = "uuid-char")
    val paymentId: UUID,
    @Column(name = "terminal_id")
    @Type(type = "uuid-char")
    val terminalId: UUID,
    @Column(name = "merchant_id")
    @Type(type = "uuid-char")
    val merchantId: UUID,
    @Column(name = "customer_id")
    @Type(type = "uuid-char")
    val customerId: UUID,
    @Column(name = "gross_amount")
    val grossAmount: String,
    val currency: String,
    val installments: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "tax_customer_id", referencedColumnName = "id")
    val taxCustomer: TaxCustomer,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "tax_merchant_id", referencedColumnName = "id")
    val taxMerchant: TaxMerchant,
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "merchant_tax_rule",
        joinColumns = [JoinColumn(name = "taxed_operation_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "tax_rule_id", referencedColumnName = "tax_id")]
    )
    val merchantTaxRules: Set<Tax>,
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST,CascadeType.REMOVE, CascadeType.DETACH, CascadeType.REFRESH])
    @JoinTable(
        name = "customer_tax_rule",
        joinColumns = [JoinColumn(name = "taxed_operation_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "tax_rule_id", referencedColumnName = "tax_id")]
    )
    val customerTaxRules: Set<Tax>,
    @Column(name = "create_datetime")
    val createDatetime: OffsetDateTime
) {

    override fun toString(): String =
        "TaxedOperation(" +
            "id: $id, " +
            "paymentId: $paymentId," +
            "terminalId: $terminalId," +
            "merchantId: $merchantId," +
            "customerId: $customerId," +
            "grossAmount: $grossAmount," +
            "currency: $currency," +
            "installments: $installments," +
            "createDatetime: $createDatetime)"

    override fun equals(other: Any?): Boolean =
        (other is TaxedOperation) &&
            id == other.id

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
