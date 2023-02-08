package com.menta.api.transactions.adapter.out.db

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.aTransactionEntity
import com.menta.api.transactions.adapter.out.db.mapper.ToTransactionEntityMapper
import com.menta.api.transactions.application.port.out.TransactionRepositoryOutPort
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class TransactionRepositorySpec : FeatureSpec({
    feature("create transaction") {
        lateinit var repository: TransactionDbRepository
        lateinit var transactionRepository: TransactionRepositoryOutPort
        lateinit var mapper: ToTransactionEntityMapper

        beforeEach {
            repository = mockk()
            mapper = mockk()

            transactionRepository = TransactionRepository(
                repository = repository,
                mapper = mapper
            )
        }
        scenario("successful create") {
            val transaction = aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID))
            val entity = aTransactionEntity()

            // given mocked dependencies
            every { mapper.map(transaction) } returns entity
            every { repository.save(entity) } returns entity

            // expect that
            transactionRepository.create(transaction) shouldBe entity

            // dependencies called
            verify(exactly = 1) { mapper.map(transaction) }
            verify(exactly = 1) { repository.save(entity) }
        }
    }
})
