package com.menta.api.transactions.adapter.out.db

import com.menta.api.transactions.adapter.out.db.mapper.ToOperationEntityMapper
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import com.menta.api.transactions.shared.util.log.CompanionLogger
import com.menta.api.transactions.shared.util.log.benchmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.ListResourceBundle
import java.util.Optional
import java.util.UUID
import com.menta.api.transactions.adapter.out.db.entity.Operation as OperationEntity

@Component
class OperationRepository(
    private val repository: OperationDbRepository,
    private val operationEntityMapper: ToOperationEntityMapper,
) : OperationRepositoryOutPort {

    override fun create(transaction: Transaction) {
        log.benchmark("Create createdPayment operation entity") {
            transaction
                .toEntityModel()
                .persist()
        }.log { info("operation entity persisted: {}", transaction.operation) }
    }

    override fun find(operationId: UUID, operationType: OperationType): Optional<OperationEntity> =
        log.benchmark("Find operation entity") {
            repository.findByIdAndType(operationId, operationType.name)
        }.log { info("operation {} of type {} found", operationId, operationType.name) }

    override fun find(acquirerId: String, operationType: OperationType): Optional<OperationEntity> =
        log.benchmark("Find operation entity") {
            repository.findByAcquirerIdAndType(acquirerId, operationType.name)
        }.log { info("operation for acquirer id {} of type {} found", acquirerId, operationType.name) }

    override fun find(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime?,
        end: OffsetDateTime?,
        status: List<StatusCode>?,
        pageable: Pageable

    ): Page<OperationEntity> =
        log.benchmark("Find operation entity by filter") {
            repository.findByFilter(
                operationType?.name,
                transactionType?.name,
                transactionId,
                merchantId,
                customerId,
                terminalId,
                start,
                end,
                status,
                pageable
            )
        }

    private fun Transaction.toEntityModel() =
        operationEntityMapper.map(this)
            .log { info("Transaction mapped to operation entity model") }

    private fun OperationEntity.persist() =
        repository.save(this)
            .log { info("operation entity persisted: {}", it.id) }

    companion object : CompanionLogger()
}
