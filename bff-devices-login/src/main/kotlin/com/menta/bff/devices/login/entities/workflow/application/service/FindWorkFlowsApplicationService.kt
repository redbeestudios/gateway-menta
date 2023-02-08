package com.menta.bff.devices.login.entities.workflow.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.workflow.application.port.out.FindWorkFlowsPortOut
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindWorkFlowsApplicationService(
    private val findWorkFlowsPortOut: FindWorkFlowsPortOut
) {

    fun find(email: Email, type: UserType, userAuth: UserAuth): Either<ApplicationError, List<WorkFlow>> =
        findWorkFlowsPortOut.findBy(email, type, userAuth)
            .logRight { info("workflows found: {}", it) }

    companion object : CompanionLogger()
}
