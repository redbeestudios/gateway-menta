package com.menta.api.taxed.operations.adapter.out.db

import com.kiwi.api.payments.shared.util.log.benchmark
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import com.menta.api.taxed.operations.adapter.out.db.mapper.ToTaxedOperationMapper
import com.menta.api.taxed.operations.applications.port.out.TaxedOperationRepositoryPortOut
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import com.menta.api.taxed.operations.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class TaxedOperationRepository(
    private val repository: TaxedOperationDbRepository,
    private val toTaxedOperationMapper: ToTaxedOperationMapper,
) : TaxedOperationRepositoryPortOut {
    override fun create(paymentTaxedOperation: PaymentTaxedOperation): TaxedOperation =
        log.benchmark("Create TaxedOperation entity to persist") {
            paymentTaxedOperation
                .toEntityModel()
                .persist()
                .log { info("successfully persisted operation") }
        }

    private fun PaymentTaxedOperation.toEntityModel(): TaxedOperation {
        return toTaxedOperationMapper.map(this)
    }

    private fun TaxedOperation.persist(): TaxedOperation =
        repository.save(this)
            .log { info("TaxedOperation entity persisted: {}", it.id) }

    companion object : CompanionLogger()
}
