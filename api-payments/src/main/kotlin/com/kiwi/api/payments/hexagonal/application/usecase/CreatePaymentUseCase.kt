package com.kiwi.api.payments.hexagonal.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.mapper.ToCreatedPaymentMapper
import com.kiwi.api.payments.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.AcquirerRepositoryPortOut
import com.kiwi.api.payments.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.payments.hexagonal.application.usecase.provider.AuthorizationPendingProvider
import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreatePaymentUseCase(
    private val acquirerRepository: AcquirerRepositoryPortOut,
    private val createdPaymentProducer: CreatedPaymentProducerPortOut,
    private val createdPaymentMapper: ToCreatedPaymentMapper,
    private val authorizationPendingProvider: AuthorizationPendingProvider
) : CreatePaymentPortIn {

    override fun execute(payment: Payment): Either<ApplicationError, CreatedPayment> =
        with(payment) {
            authorize()
                .fold(
                    { applicationError ->
                        produce(payment, buildAuthorizationPending())
                        applicationError.left()
                    },
                    { authorization
                        ->
                        produce(payment, authorization)
                    }
                )
        }

    private fun buildAuthorizationPending() = authorizationPendingProvider.provide()

    private fun produce(payment: Payment, authorization: Authorization) =
        mapToCreatePayment(payment, authorization)
            .produceMessage()

    private fun mapToCreatePayment(payment: Payment, authorization: Authorization) =
        createdPaymentMapper.mapFromPayment(payment, authorization)

    private fun Payment.authorize(): Either<ApplicationError, Authorization> =
        acquirerRepository.authorize(this)
            .log { info("payment authorized: {}", it) }

    private fun CreatedPayment.produceMessage(): Either<ApplicationError, CreatedPayment> =
        this.right()
            .also {
                createdPaymentProducer.produce(this)
                    .logEither(
                        { error("Error with created.payment queue") },
                        { info("Sent payment into queue created.payment") }
                    )
                    .mapLeft { it.left() }
            }

    companion object : CompanionLogger()
}
