package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedAnnulmentsRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.mapper.ToCreatedAnnulmentMapper
import com.kiwi.api.reimbursements.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateAnnulmentUseCase(
    private val acquirerRepository: AcquirerRepository,
    private val createdAnnulmentsRepository: CreatedAnnulmentsRepositoryPortOut,
    private val idProvider: IdProvider,
    private val toCreatedAnnulmentMapper: ToCreatedAnnulmentMapper
) : CreateAnnulmentPortIn {

    override fun execute(annulment: Annulment): Either<ApplicationError, CreatedAnnulment> =
        with(annulment) {
            createAnnulment(this, authorize())
                .produceMessage()
        }

    private fun createAnnulment(annulment: Annulment, authorization: Authorization) =
        toCreatedAnnulmentMapper.from(annulment, authorization, idProvider.provide())
            .log { info("annulment created: {}", it) }
    private fun CreatedAnnulment.produceMessage(): Either<ApplicationError, CreatedAnnulment> =
        createdAnnulmentsRepository.save(this)
            .map { this }
            .logEither(
                { error("Error with created.annulments queue") },
                { info("Sent annulment into queue created.annulments") }
            )

    private fun Annulment.authorize() =
        acquirerRepository.authorizeAnnulment(this)
            .log { info("annulment authorized: {}", it.authorizationCode) }

    companion object : CompanionLogger()
}
