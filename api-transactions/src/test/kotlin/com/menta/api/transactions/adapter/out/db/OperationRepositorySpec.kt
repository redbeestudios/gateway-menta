package com.menta.api.transactions.adapter.out.db

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.TestConstants.Companion.OPERATION_ID
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.out.db.mapper.ToOperationEntityMapper
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.domain.OperationType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

class OperationRepositorySpec : FeatureSpec({
    feature("create transaction") {
        lateinit var repository: OperationDbRepository
        lateinit var mapper: ToOperationEntityMapper
        lateinit var operationRepository: OperationRepositoryOutPort

        beforeEach {
            repository = mockk()
            mapper = mockk()

            operationRepository = OperationRepository(
                repository = repository,
                operationEntityMapper = mapper
            )
        }
        scenario("successful create") {
            val transaction = aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID))
            val entity = aOperationEntity()

            // given mocked dependencies
            every { mapper.map(transaction) } returns entity
            every { repository.save(entity) } returns entity

            // expect that
            operationRepository.create(transaction) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { mapper.map(transaction) }
            verify(exactly = 1) { repository.save(entity) }
        }
        feature("find by operation id and operation type") {

            scenario("transaction found") {
                val operationId = UUID.fromString(OPERATION_ID)
                val operationType = OperationType.PAYMENT

                val operation = aOperationEntity()

                every {
                    repository.findByIdAndType(operationId, operationType.name)
                } returns Optional.of(operation)

                operationRepository.find(operationId, operationType) shouldBe Optional.of(operation)

                verify(exactly = 1) { repository.findByIdAndType(operationId, operationType.name) }
            }

            scenario("transaction NOT found") {
                val operationId = UUID.fromString(OPERATION_ID)
                val operationType = OperationType.PAYMENT

                every {
                    repository.findByIdAndType(operationId, operationType.name)
                } returns Optional.empty()

                operationRepository.find(operationId, operationType) shouldBe Optional.empty()

                verify(exactly = 1) { repository.findByIdAndType(operationId, operationType.name) }
            }

            scenario("transaction found") {
                val transactionId = UUID.fromString(TRANSACTION_ID)
                val page = 0
                val size = 10
                val pageable = PageRequest.of(page, size)
                val operation = aOperationEntity()
                val result = PageImpl(listOf(operation))


                every {
                    repository.findByFilter(
                        null,
                        null,
                        transactionId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        pageable
                    )
                } returns result

                operationRepository.find(null,
                    null,
                    transactionId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    pageable) shouldBe result

                verify(exactly = 1) { repository.findByFilter( null,
                    null,
                    transactionId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    pageable) }
            }
        }
    }
})
