package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedRefundMapper
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedRefundProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateRefundUseCase(
    private val acquirerRepository: AcquirerRepository,
    private val toCreatedRefundMapper: ToCreatedRefundMapper,
    private val producer: CreatedRefundProducerPortOut
) : CreateRefundPortIn {

    override fun execute(refund: Refund): Either<ApplicationError, CreatedRefund> =
        with(refund) {
            authorize()
                .toCreatedRefund(refund)
                .produceMessage()
        }

    private fun Refund.authorize() =
        acquirerRepository.authorize(this)
            .log { info("refund authorized: {}", it.authorizationCode) }

    private fun Authorization.toCreatedRefund(refund: Refund) =
        toCreatedRefundMapper.map(refund, this)
            .log { info("refund reversal created: {}", it) }

    private fun CreatedRefund.produceMessage(): Either<ApplicationError, CreatedRefund> =
        producer.produce(this)
            .logEither(
                { error("Failed to create refund reversal message: {}", it) },
                { info("Refund reversal message sent {}", it) }
            ).map { this }

    companion object : CompanionLogger()
}
