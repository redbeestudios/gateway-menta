package com.menta.api.taxed.operations.adapter.out.db.entity

import com.menta.api.taxed.operations.domain.Country
import com.menta.api.taxed.operations.domain.FiscalCondition
import com.menta.api.taxed.operations.domain.PaymentMethod
import com.menta.api.taxed.operations.domain.TaxType
import org.hibernate.annotations.Type
import java.math.BigDecimal
import java.util.Objects
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tax_rule")
data class Tax(
    @Id
    @Column(name = "tax_id")
    @Type(type = "uuid-char")
    val id: UUID,
    val name: String,
    val description: String? = null,
    val percentage: BigDecimal = BigDecimal.ZERO,
    @Column(name = "fiscal_condition")
    val fiscalCondition: FiscalCondition,
    val country: Country,
    @Column(name = "payment_method")
    val paymentMethod: PaymentMethod,
    @Column(name = "tax_type")
    val taxType: TaxType
) {
    override fun toString(): String =
        "Tax(" +
            "id: $id," +
            "name: $name," +
            "description: $description," +
            "percentage: $percentage," +
            "fiscalCondition: $fiscalCondition," +
            "country: $country," +
            "paymentMethod: $paymentMethod," +
            "taxType: $taxType)"

    override fun equals(other: Any?): Boolean =
        (other is Tax) &&
            id == other.id

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
