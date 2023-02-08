package com.menta.apiacquirers.application.usecase

import arrow.core.Either
import com.menta.apiacquirers.application.port.`in`.FindCustomerPortIn
import com.menta.apiacquirers.application.port.out.FindCustomerPortOut
import com.menta.apiacquirers.domain.Customer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCustomerUseCase(
    private val findCustomer: FindCustomerPortOut
) : FindCustomerPortIn {

    override fun execute(id: UUID): Either<ApplicationError, Customer> =
        findCustomer.findBy(id)
            .logRight { info("customer found: {}", it) }

    companion object : CompanionLogger()
}
