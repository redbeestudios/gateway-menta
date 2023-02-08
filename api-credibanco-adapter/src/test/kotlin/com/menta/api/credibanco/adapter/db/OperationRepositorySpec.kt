package com.menta.api.credibanco.adapter.db

import com.menta.api.credibanco.TestConstants.Companion.OPERATION_RESPONSE_ID
import com.menta.api.credibanco.aCreatedOperation
import com.menta.api.credibanco.aResponseOperation
import com.menta.api.credibanco.adapter.controller.provider.IdProvider
import com.menta.api.credibanco.adapter.db.mapper.ToResponseOperationMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class OperationRepositorySpec : FeatureSpec({

    val operationDbRepository: OperationDbRepository = mockk()
    val idProvider: IdProvider = mockk()
    val credibancoOperationMapper = ToResponseOperationMapper(idProvider)

    val operationRepository = OperationRepository(
        repository = operationDbRepository,
        toResponseOperationMapper = credibancoOperationMapper
    )

    beforeEach { clearAllMocks() }

    feature("save credibanco operation") {
        scenario("successful operation save") {
            val createdOperation = aCreatedOperation()
            val credibancoOperation = aResponseOperation()
            every { idProvider.provide() } returns UUID.fromString(OPERATION_RESPONSE_ID)
            every { operationDbRepository.save(credibancoOperation) } returns credibancoOperation.copy(
                id = UUID.fromString(
                    OPERATION_RESPONSE_ID
                )
            )
            operationRepository.save(createdOperation) shouldBe Unit

            verify(exactly = 1) { operationDbRepository.save(credibancoOperation) }
        }
    }
})
