package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import arrow.core.Either
import com.kiwi.api.reversal.hexagonal.application.mapper.ToCreatedAnnulmentMapper
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedAnnulmentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateAnnulmentUseCase(
    private val acquirerRepository: AcquirerRepository,
    private val toCreatedAnnulmentMapper: ToCreatedAnnulmentMapper,
    private val producer: CreatedAnnulmentProducerPortOut
) : CreateAnnulmentPortIn {

    override fun execute(annulment: Annulment): Either<ApplicationError, CreatedAnnulment> =
        with(annulment) {
            authorize()
                .toCreatedAnnulment(annulment)
                .produceMessage()
        }

    private fun Annulment.authorize() =
        acquirerRepository.authorize(this)
            .log { info("annulment authorized: {}", it.authorizationCode) }

    private fun Authorization.toCreatedAnnulment(annulment: Annulment) =
        toCreatedAnnulmentMapper.map(annulment, this)
            .log { info("annulment reversal created: {}", it) }

    private fun CreatedAnnulment.produceMessage(): Either<ApplicationError, CreatedAnnulment> =
        producer.produce(this)
            .logEither(
                { error("Failed to create annulment reversal message: {}", it) },
                { info("Annulment reversal message sent {}", it) }
            ).map { this }

    companion object : CompanionLogger()
}
