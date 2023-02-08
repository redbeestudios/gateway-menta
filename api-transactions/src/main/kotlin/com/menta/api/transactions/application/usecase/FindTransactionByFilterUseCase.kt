package com.menta.api.transactions.application.usecase

import arrow.core.Either
import com.menta.api.transactions.adapter.out.db.entity.Operation
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.`in`.FindTransactionByFilterInPort
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import com.menta.api.transactions.shared.error.model.ApplicationError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import com.menta.api.transactions.shared.util.either.rightIfPresent
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Component
@Transactional
class FindTransactionByFilterUseCase(
    private val transactionMapper: TransactionMapper,
    private val operationRepository: OperationRepositoryOutPort
) : FindTransactionByFilterInPort {
    override fun execute(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime?,
        end: OffsetDateTime?,
        page: Int,
        size: Int,
        status: List<StatusCode>?
    ): Either<ApplicationError, Page<Transaction>> =
        findBy(
            operationType,
            transactionType,
            transactionId,
            merchantId,
            customerId,
            terminalId,
            getStartDateOrLastWeek(start),
            getEndDateOrNow(end),
            sortedByDesc(page, size),
            status
        ).map { it.map { transactionMapper.map(it) } }

    private fun findBy(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime,
        end: OffsetDateTime,
        pageable: Pageable,
        status: List<StatusCode>?
    ) =
        operationRepository.find(
            operationType,
            transactionType,
            transactionId,
            merchantId,
            customerId,
            terminalId,
            start,
            end,
            status,
            pageable
        ).shouldBePresent(
            operationType,
            transactionType,
            transactionId,
            merchantId,
            customerId,
            terminalId,
            start,
            end,
            status
        )
            .log { info("operation of type searched") }

    private fun Page<Operation>.shouldBePresent(
        operationType: OperationType?,
        transactionType: TransactionType?,
        transactionId: UUID?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        start: OffsetDateTime,
        end: OffsetDateTime,
        status: List<StatusCode>?
    ) =
        rightIfPresent(
            error = operationNotFound(
                operationType,
                transactionType,
                transactionId,
                merchantId,
                customerId,
                terminalId,
                status,
                start,
                end
            )
        )
            .logEither(
                {
                    error(
                        "operation with operationType: {}, transactionType: {}, transactionId {}, merchantId {}, customerId {}, terminalId {} between start {} " +
                                "and end {} not found",
                        operationType,
                        transactionId,
                        merchantId,
                        customerId,
                        terminalId,
                        start,
                        end
                    )
                },
                {
                    info(
                        "operation with operationType: {}, transactionType: {}, transactionId {}, merchantId {}, customerId {}, " +
                                "terminalId {} between start {} and end {} not found",
                        operationType,
                        transactionType,
                        transactionId,
                        merchantId,
                        customerId,
                        terminalId,
                        start,
                        end
                    )
                }
            )

    private fun getStartDateOrLastWeek(start: OffsetDateTime?) =
        start ?: OffsetDateTime.now().minusWeeks(1)

    private fun getEndDateOrNow(end: OffsetDateTime?) =
        end ?: OffsetDateTime.now()

    private fun sortedByDesc(page: Int, size: Int): Pageable =
        PageRequest.of(page, size, Sort.by("datetime").descending())

    companion object : CompanionLogger()
}
