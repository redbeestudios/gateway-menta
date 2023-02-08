package com.menta.api.transactions.application

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.out.db.mapper.TransactionMapper
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.application.usecase.FindTransactionByFilterUseCase
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.TransactionType
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.OffsetDateTime
import java.util.UUID

class FindTransactionByFilterUseCaseSpec : FeatureSpec({

    val repository = mockk<OperationRepositoryOutPort>()
    val mapper = mockk<TransactionMapper>()

    val useCase = FindTransactionByFilterUseCase(mapper, repository)

    beforeEach { clearAllMocks() }

    feature("find transaction by filters") {

        scenario("transaction found") {
            val operationType = OperationType.valueOf(TestConstants.OPERATION_TYPE)
            val transactionType = TransactionType.valueOf(TestConstants.TRANSACTION_TYPE)
            val transactionId = UUID.fromString(TestConstants.TRANSACTION_ID)
            val merchantId = UUID.fromString(TestConstants.MERCHANT_ID)
            val customerId = UUID.fromString(TestConstants.CUSTOMER_ID)
            val terminalId = UUID.fromString(TestConstants.TERMINAL_ID)
            val start = OffsetDateTime.now()
            val end = OffsetDateTime.now().plusHours(1)
            val page = 0
            val size = 100
            val listStatus = listOf(StatusCode.APPROVED)
            val operation = aOperationEntity()
            val transaction = aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID))

            every {
                repository.find(
                    operationType,
                    transactionType,
                    transactionId,
                    merchantId,
                    customerId,
                    terminalId,
                    start,
                    end,
                    listStatus,
                    any()
                )
            } returns PageImpl(
                listOf(operation)
            )

            every { mapper.map(operation) } returns transaction

            useCase.execute(
                operationType,
                transactionType,
                transactionId,
                merchantId,
                customerId,
                terminalId,
                start,
                end,
                page,
                size,
                listStatus
            ) shouldBeRight PageImpl(listOf(transaction))

            verify {
                repository.find(
                    operationType,
                    transactionType,
                    transactionId,
                    merchantId,
                    customerId,
                    terminalId,
                    start,
                    end,
                    listStatus,
                    any()
                )
            }
        }

        scenario("transaction by filter not found") {
            val operationType = OperationType.valueOf(TestConstants.OPERATION_TYPE)
            val transactionId = UUID.fromString(TestConstants.TRANSACTION_ID)
            val transactionType = TransactionType.ACQUIRER
            val merchantId = UUID.fromString(TestConstants.MERCHANT_ID)
            val customerId = UUID.fromString(TestConstants.CUSTOMER_ID)
            val terminalId = UUID.fromString(TestConstants.TERMINAL_ID)
            val start = OffsetDateTime.now()
            val end = OffsetDateTime.now().plusHours(1)
            val page = 0
            val size = 100
            val listStatus = listOf(StatusCode.APPROVED)

            val error = operationNotFound(
                operationType,
                transactionType,
                transactionId,
                merchantId,
                customerId,
                terminalId,
                listStatus,
                start,
                end
            )

            every {
                repository.find(
                    operationType,
                    transactionType,
                    transactionId,
                    merchantId,
                    customerId,
                    terminalId,
                    start,
                    end,
                    listStatus,
                    any()
                )
            } returns PageImpl(emptyList())

            useCase.execute(
                operationType,
                transactionType,
                transactionId,
                merchantId,
                customerId,
                terminalId,
                start,
                end,
                page,
                size,
                listStatus,
            ) shouldBeLeft error

            verify {
                repository.find(
                    operationType,
                    transactionType,
                    transactionId,
                    merchantId,
                    customerId,
                    terminalId,
                    start,
                    end,
                    listStatus,
                    any()
                )
            }
        }
    }
})
