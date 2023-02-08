package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedPaymentMapper
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreatePaymentUseCase(
    private val acquirerRepository: AcquirerRepository,
    private val producer: CreatedPaymentProducerPortOut,
    private val toCreatedPaymentMapper: ToCreatedPaymentMapper
) : CreatePaymentPortIn {

    override fun execute(payment: Payment): Either<ApplicationError, CreatedPayment> =
        with(payment) {
            authorize()
                .toCreatedPayment(payment)
                .produceMessage()
        }

    private fun Payment.authorize() =
        acquirerRepository.authorize(this)
            .log { info("payment reverse authorized: {}", it) }

    private fun Authorization.toCreatedPayment(payment: Payment) =
        toCreatedPaymentMapper.map(payment, this)
            .log { info("payment reversal created: {}", it) }

    private fun CreatedPayment.produceMessage(): Either<ApplicationError, CreatedPayment> =
        producer.produce(this)
            .logEither(
                { error("Failed to create payment reversal message: {}", it) },
                { info("Payment reversal message sent {}", it) }
            )
            .map { this }

    companion object : CompanionLogger()
}
