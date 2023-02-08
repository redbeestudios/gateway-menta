package com.menta.bff.devices.login.entities.acquirers.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.acquirers.application.port.out.FindAcquirersOperablePortOut
import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirersOperableApplicationService(
    private val findCustomer: FindAcquirersOperablePortOut
) {

    fun findBy(customerId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerOperable>> =
        findCustomer.findBy(customerId, userAuth)
            .logRight { info("acquirers operable found: {}", it) }

    companion object : CompanionLogger()
}
