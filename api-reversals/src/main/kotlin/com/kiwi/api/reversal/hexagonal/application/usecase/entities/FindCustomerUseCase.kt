package com.kiwi.api.reversal.hexagonal.application.usecase.entities

import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.CustomerRepositoryPortOut
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val customerRepository: CustomerRepositoryPortOut,
) : FindCustomerPortIn {

    override fun execute(customerId: UUID): Customer =
        customerRepository.retrieve(customerId)
}
