package com.menta.api.transactions.adapter.out.db

import com.menta.api.transactions.adapter.out.db.entity.Operation
import com.menta.api.transactions.adapter.out.db.entity.Transaction
import com.menta.api.transactions.domain.StatusCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

interface TransactionDbRepository : JpaRepository<Transaction, UUID>
interface OperationDbRepository : JpaRepository<Operation, UUID> {
    fun findByIdAndType(operationId: UUID, operationType: String): Optional<Operation>
    fun findByAcquirerIdAndType(acquirerId: String, operationType: String): Optional<Operation>

    @Query(
        "SELECT o FROM Operation o WHERE " +
                "(:operationType = null or o.type = :operationType) AND " +
                "(:transactionId = null or o.transaction.transactionId = :transactionId) AND " +
                "(:transactionType = null or o.transaction.type = :transactionType) AND " +
                "(:merchantId = null or o.transaction.merchantId = :merchantId) AND " +
                "(:customerId = null or o.transaction.customerId = :customerId) AND " +
                "(:terminalId = null or o.transaction.terminalId = :terminalId) AND " +
                "(:start = null or o.transaction.createdDatetime >= :start) AND " +
                "(:end = null or o.transaction.createdDatetime <= :end) AND " +
                "(COALESCE(:status, null) = null or o.status IN (:status))"
    )
    fun findByFilter(
        @Param("operationType") operationType: String?,
        @Param("transactionType") transactionType: String?,
        @Param("transactionId") transactionId: UUID?,
        @Param("merchantId") merchantId: UUID?,
        @Param("customerId") customerId: UUID?,
        @Param("terminalId") terminalId: UUID?,
        @Param("start") start: OffsetDateTime?,
        @Param("end") end: OffsetDateTime?,
        status: List<StatusCode>?,
        pageable: Pageable
    ): Page<Operation>
}
