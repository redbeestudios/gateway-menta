package com.kiwi.api.payments.hexagonal.adapter.out.db

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.adapter.out.db.mapper.ToOperationEntityMapper
import com.kiwi.api.payments.hexagonal.application.port.out.OperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.OperationNotFound
import com.kiwi.api.payments.shared.util.either.rightIfPresent
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.UUID
import com.kiwi.api.payments.hexagonal.adapter.out.db.entity.Operation as OperationEntity

@Component
class OperationRepository(
    private val repository: OperationDbRepository,
    private val operationEntityMapper: ToOperationEntityMapper,
) : OperationRepositoryPortOut {

    override fun create(createdPayment: CreatedPayment) =
        log.benchmark("Create operation") {
            createdPayment
                .toEntityModel()
                .persist()
        }.log { info("operation persisted: {}", createdPayment) }

    override fun getId(reversalOperation: ReversalOperation): Either<ApplicationError, UUID> =
        log.benchmark("Get operation id of Operation") {
            getBy(
                reversalOperation.trace,
                reversalOperation.ticket,
                reversalOperation.batch,
                reversalOperation.terminal.id,
                reversalOperation.amount.total,
                reversalOperation.datetime
            ).rightIfPresent(OperationNotFound())
                .map { it.id }
        }

    override fun updateStatusForReversal(reversalOperation: ReversalOperation): Either<ApplicationError, Unit> =
        log.benchmark("Update operation") {
            getById(reversalOperation.operationId!!)
                .map { toEntityModel(it).persist() }
        }.log { info("Operation update status with REVERSAL: {}", reversalOperation.operationId) }

    private fun getById(id: UUID): Either<ApplicationError, OperationEntity> =
        repository.findById(id).rightIfPresent(OperationNotFound())

    private fun getBy(
        trace: String,
        ticket: String,
        batch: String,
        terminal: UUID,
        amount: String,
        datetime: OffsetDateTime
    ) = repository.findByTraceAndTicketAndBatchAndTerminalIdAndAmountAndDatetime(trace, ticket, batch, terminal, amount, datetime)

    private fun CreatedPayment.toEntityModel() = operationEntityMapper.map(this)
    private fun toEntityModel(operation: OperationEntity) =
        operationEntityMapper.map(operation)

    private fun OperationEntity.persist() {
        repository.save(this)
    }

    companion object : CompanionLogger()
}
