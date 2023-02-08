package com.menta.bff.devices.login.entities.tags.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.tags.application.port.out.FindAcquirerTagPortOut
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirerTagApplicationService(
    private val findAcquirerTag: FindAcquirerTagPortOut
) {

    fun findTagEmvBy(customerId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerTag>> =
        findAcquirerTag.findBy(customerId, TYPE_EMV, userAuth)
            .logRight { info("acquirers tags found: {}", it) }

    companion object : CompanionLogger() {
        private const val TYPE_EMV = "EMV"
    }
}
