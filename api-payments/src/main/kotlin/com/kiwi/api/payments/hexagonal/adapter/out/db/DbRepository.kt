package com.kiwi.api.payments.hexagonal.adapter.out.db

import com.kiwi.api.payments.hexagonal.adapter.out.db.entity.Operation
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

interface OperationDbRepository : JpaRepository<Operation, UUID> {
    fun findByTraceAndTicketAndBatchAndTerminalIdAndAmountAndDatetime(
        trace: String,
        ticket: String,
        batch: String,
        terminal: UUID,
        amount: String,
        datetime: OffsetDateTime
    ): Optional<Operation>
}
