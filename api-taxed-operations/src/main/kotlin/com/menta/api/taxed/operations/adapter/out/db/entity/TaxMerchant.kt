package com.menta.api.taxed.operations.adapter.out.db.entity

import com.menta.api.taxed.operations.domain.PaymentMethod
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "tax_merchant")
data class TaxMerchant(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "taxed_amount")
    val taxedAmount: BigDecimal,
    val iva: BigDecimal,
    val ganancias: BigDecimal,
    val commission: BigDecimal,
    @Column(name = "gross_commission")
    val grossCommission: BigDecimal,
    @Column(name = "gross_commission_with_tax")
    val grossCommissionWithTax: BigDecimal,
    @Column(name = "partial_gross_amount")
    val partialGrossAmount: BigDecimal,
    @Column(name = "iva_commission")
    val ivaCommission: BigDecimal,
    @Column(name = "payment_method")
    val paymentMethod: PaymentMethod,
    val term: Int,
    val installments: Int,
    val discount: BigDecimal,
    @Column(name = "next_payment_date")
    val nextPaymentDate: OffsetDateTime,
    @OneToOne(mappedBy = "taxMerchant")
    val taxedOperation: TaxedOperation?
)
