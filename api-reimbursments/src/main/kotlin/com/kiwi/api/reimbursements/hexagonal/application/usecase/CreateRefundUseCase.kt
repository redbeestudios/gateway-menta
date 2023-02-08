package com.kiwi.api.reimbursements.hexagonal.application.usecase

import arrow.core.Either
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedRefundRepositoryPorOut
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.mapper.ToCreatedRefundMapper
import com.kiwi.api.reimbursements.hexagonal.domain.provider.IdProvider
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateRefundUseCase(
    private val acquirerRepository: AcquirerRepository,
    private val createdRefundRepository: CreatedRefundRepositoryPorOut,
    private val idProvider: IdProvider,
    private val toCreatedRefundMapper: ToCreatedRefundMapper
) : CreateRefundPortIn {

    override fun execute(refund: Refund): Either<ApplicationError, CreatedRefund> =
        with(refund) {
            createRefund(this, authorize())
                .produceMessage()
        }

    private fun Refund.authorize() =
        acquirerRepository.authorizeRefund(this)
            .log { info("refund authorized: {}", this) }

    private fun createRefund(refund: Refund, authorization: Authorization) =
        toCreatedRefundMapper.from(refund, authorization, idProvider.provide())
            .log { info("refund created: {}", it) }

    private fun CreatedRefund.produceMessage() =
        createdRefundRepository.save(this)
            .map { this }
            .logEither(
                { error("Error with created.refund queue") },
                { info("Sent annulment into queue created.refund") }
            )

    companion object : CompanionLogger()
}
