package com.kiwi.api.reversal.hexagonal.adapter.out.db

import com.kiwi.api.reversal.hexagonal.adapter.out.db.mapper.ToOperationEntityMapper
import com.kiwi.api.reversal.hexagonal.application.port.out.OperationRepositoryOutPort
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import com.kiwi.api.reversal.shared.util.log.benchmark
import org.springframework.stereotype.Component
import com.kiwi.api.reversal.hexagonal.adapter.out.db.entity.Operation as OperationEntity

@Component
class OperationRepository(
    private val repository: OperationDbRepository,
    private val operationEntityMapper: ToOperationEntityMapper,
) : OperationRepositoryOutPort {

    override fun create(createdPayment: CreatedPayment) =
        log.benchmark("Create payment operation entity") {
            createdPayment
                .toEntityModel()
                .persist()
        }.log { info("payment operation entity persisted: {}", createdPayment) }

    override fun create(createdRefund: CreatedRefund) =
        log.benchmark("Create refund operation entity") {
            createdRefund
                .toEntityModel()
                .persist()
        }.log { info("refund operation entity persisted: {}", createdRefund) }

    override fun create(createdAnnulment: CreatedAnnulment) =
        log.benchmark("Create annulment operation entity") {
            createdAnnulment
                .toEntityModel()
                .persist()
        }.log { info("annulment operation entity persisted: {}", createdAnnulment) }

    private fun CreatedPayment.toEntityModel() = operationEntityMapper.map(this)

    private fun CreatedRefund.toEntityModel() = operationEntityMapper.map(this)

    private fun CreatedAnnulment.toEntityModel() = operationEntityMapper.map(this)

    private fun OperationEntity.persist() {
        repository.save(this)
    }

    companion object : CompanionLogger()
}
