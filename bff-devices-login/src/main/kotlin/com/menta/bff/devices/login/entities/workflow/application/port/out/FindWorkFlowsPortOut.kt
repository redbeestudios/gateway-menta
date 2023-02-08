package com.menta.bff.devices.login.entities.workflow.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface FindWorkFlowsPortOut {
    fun findBy(email: Email, type: UserType, userAuth: UserAuth): Either<ApplicationError, List<WorkFlow>>
}
