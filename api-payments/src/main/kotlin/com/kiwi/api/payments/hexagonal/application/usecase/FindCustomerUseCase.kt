package com.kiwi.api.payments.hexagonal.application.usecase

import com.kiwi.api.payments.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.payments.hexagonal.application.port.out.CustomerRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.Payment
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val customerRepository: CustomerRepositoryPortOut,
) : FindCustomerPortIn {

    override fun execute(customerId: UUID): Payment.Customer =
        customerRepository.retrieve(customerId)
}
