package com.menta.api.credibanco.adapter.db

import com.menta.api.credibanco.adapter.db.entity.ResponseOperation
import com.menta.api.credibanco.adapter.db.mapper.ToResponseOperationMapper
import com.menta.api.credibanco.application.port.out.OperationRepositoryPortOut
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import com.menta.api.credibanco.shared.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class OperationRepository(
    private val repository: OperationDbRepository,
    private val toResponseOperationMapper: ToResponseOperationMapper,
) : OperationRepositoryPortOut {

    override fun save(createdOperation: CreatedOperation) =
        log.benchmark("Save credibanco response operation") {
            createdOperation
                .toEntityModel()
                .persist()
        }.log { info("response operation persisted: {}", createdOperation) }

    private fun CreatedOperation.toEntityModel() = toResponseOperationMapper.map(this)

    private fun ResponseOperation.persist() {
        repository.save(this)
    }

    companion object : CompanionLogger()
}
