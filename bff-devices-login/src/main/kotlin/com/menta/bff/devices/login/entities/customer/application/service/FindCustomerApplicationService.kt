package com.menta.bff.devices.login.entities.customer.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.customer.application.port.out.FindCustomerPortOut
import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCustomerApplicationService(
    private val findCustomer: FindCustomerPortOut
) {

    fun findBy(id: UUID): Either<ApplicationError, Customer> =
        findCustomer.findBy(id)
            .logRight { info("customer found: {}", it) }

    companion object : CompanionLogger()
}
