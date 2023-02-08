package com.kiwi.api.reimbursements.hexagonal.adapter.out.db

import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.mapper.ToOperationMapper
import com.kiwi.api.reimbursements.hexagonal.application.port.out.OperationRepositoryOutPort
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import com.kiwi.api.reimbursements.shared.util.log.benchmark
import org.springframework.stereotype.Component
import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.entity.Operation as OperationEntity

@Component
class OperationRepository(
    private val repository: OperationDbRepository,
    private val operationMapper: ToOperationMapper,
) : OperationRepositoryOutPort {

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

    private fun CreatedRefund.toEntityModel() = operationMapper.map(this)

    private fun CreatedAnnulment.toEntityModel() = operationMapper.map(this)

    private fun OperationEntity.persist() { repository.save(this) }

    companion object : CompanionLogger()
}
