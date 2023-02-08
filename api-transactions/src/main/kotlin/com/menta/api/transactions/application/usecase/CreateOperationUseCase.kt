package com.menta.api.transactions.application.usecase

import com.menta.api.transactions.application.port.`in`.CreateOperationInPort
import com.menta.api.transactions.application.port.out.OperationRepositoryOutPort
import com.menta.api.transactions.domain.Transaction
import org.springframework.stereotype.Component

@Component
class CreateOperationUseCase(
    private val operationRepository: OperationRepositoryOutPort
) : CreateOperationInPort {
    override fun execute(transaction: Transaction) = operationRepository.create(transaction)
}
