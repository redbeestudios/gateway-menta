package com.kiwi.api.reimbursements.hexagonal.application.usecase

import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CustomerRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val customerRepository: CustomerRepositoryPortOut,
) : FindCustomerPortIn {

    override fun execute(customerId: UUID): Customer =
        customerRepository.retrieve(customerId)
}
