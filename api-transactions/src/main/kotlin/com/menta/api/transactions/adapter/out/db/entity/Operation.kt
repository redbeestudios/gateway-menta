package com.menta.api.transactions.adapter.out.db.entity

import com.menta.api.transactions.domain.StatusCode
import org.hibernate.annotations.Type
import java.time.OffsetDateTime
import java.util.Objects
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "operations")
open class Operation(
    @Id
    @Column(name = "operation_id")
    @Type(type = "uuid-char")
    open val id: UUID,
    open val ticketId: Int?,
    @Column(name = "operation_type")
    open val type: String,
    @Enumerated(EnumType.STRING)
    open val status: StatusCode,
    @Column(name = "operation_datetime")
    open val datetime: OffsetDateTime,
    @Column(name = "acquirer_id")
    open val acquirerId: String?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = true, insertable = true)
    open val transaction: Transaction
) {
    override fun equals(other: Any?): Boolean =
        (other is Operation) &&
            id == other.id &&
            ticketId == other.ticketId &&
            type == other.type &&
            status == other.status &&
            datetime == other.datetime &&
            acquirerId == other.acquirerId &&
            transaction == other.transaction

    override fun toString() =
        "Operation(id: $id, " +
            "ticketId: $ticketId, " +
            "type: $type, " +
            "status: $status, " +
            "datetime: $datetime, " +
            "acquirerId: $acquirerId, " +
            "transaction: $transaction)"

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
