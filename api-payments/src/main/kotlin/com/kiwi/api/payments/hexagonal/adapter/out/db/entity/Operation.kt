package com.kiwi.api.payments.hexagonal.adapter.out.db.entity

import com.kiwi.api.payments.hexagonal.domain.InputMode
import com.kiwi.api.payments.hexagonal.domain.Origin
import org.hibernate.annotations.Type
import java.time.OffsetDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "operations")
open class Operation(
    @Id
    @Column(name = "operation_id")
    @Type(type = "uuid-char")
    open val id: UUID,
    open val ticketId: Int?,
    open val acquirerId: String?,
    @Enumerated(EnumType.STRING)
    open val origin: Origin,
    open val authorizationCode: String?,
    open val displayMessage: String?,
    open val statusCode: String,
    open val situationCode: String?,
    open val situationMessage: String?,
    @Type(type = "uuid-char")
    open val merchantId: UUID,
    @Type(type = "uuid-char")
    open val terminalId: UUID,
    @Type(type = "uuid-char")
    open val customerId: UUID,
    open val amount: String,
    open val currency: String,
    open val trace: String,
    open val ticket: String,
    open val batch: String,
    open val installments: String,
    open val identityType: String?,
    open val identityNumber: String?,
    @Enumerated(EnumType.STRING)
    open val inputMode: InputMode,
    open val cardHolderName: String,
    open val cardBrand: String,
    open val cardBank: String?,
    open val cardType: String,
    open val cardPan: String?,
    open val tip: String?,
    open val advance: String?,
    @Column(name = "operation_datetime")
    open val datetime: OffsetDateTime,
    @Column(name = "create_datetime")
    open val createDatetime: OffsetDateTime
)
