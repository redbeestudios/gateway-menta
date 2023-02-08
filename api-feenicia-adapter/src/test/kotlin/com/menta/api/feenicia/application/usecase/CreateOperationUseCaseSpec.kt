package com.menta.api.feenicia.application.usecase

import arrow.core.right
import com.menta.api.feenicia.application.aCreatedOperation
import com.menta.api.feenicia.application.aPaymentOperation
import com.menta.api.feenicia.application.aRefundOperation
import com.menta.api.feenicia.application.aReverseOperation
import com.menta.api.feenicia.application.port.out.FeeniciaClientRepository
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.domain.OperationType.REVERSAL
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOperationUseCaseSpec : FeatureSpec({

    feature("operation creation") {

        lateinit var feeniciaClientRepository: FeeniciaClientRepository

        lateinit var useCase: CreateOperationUseCase

        beforeEach {
            feeniciaClientRepository = mockk()

            useCase = CreateOperationUseCase(
                feeniciaClientRepository = feeniciaClientRepository
            )
        }

        scenario("successful payment operation creation") {
            // given
            val createdOperation = aCreatedOperation().right()
            val operation = aPaymentOperation()

            // when
            every { feeniciaClientRepository.execute(operation, PAYMENT) } returns createdOperation

            // then
            useCase.execute(operation, PAYMENT) shouldBe createdOperation

            verify(exactly = 1) { feeniciaClientRepository.execute(operation, PAYMENT) }
        }

        scenario("successful refund operation creation") {
            // given
            val createdOperation = aCreatedOperation().right()
            val operation = aRefundOperation()

            // when
            every { feeniciaClientRepository.execute(operation, REFUND) } returns createdOperation

            // then
            useCase.execute(operation, REFUND) shouldBe createdOperation

            verify(exactly = 1) { feeniciaClientRepository.execute(operation, REFUND) }
        }

        scenario("successful reverse operation creation") {
            // given
            val createdOperation = aCreatedOperation().right()
            val operation = aReverseOperation()

            // when
            every { feeniciaClientRepository.execute(operation, REVERSAL) } returns createdOperation

            // then
            useCase.execute(operation, REVERSAL) shouldBe createdOperation

            verify(exactly = 1) { feeniciaClientRepository.execute(operation, REVERSAL) }
        }
    }
})
