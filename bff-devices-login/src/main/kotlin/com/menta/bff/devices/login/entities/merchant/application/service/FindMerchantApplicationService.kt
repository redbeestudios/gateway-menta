package com.menta.bff.devices.login.entities.merchant.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.merchant.application.out.FindMerchantPortOut
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindMerchantApplicationService(
    private val findMerchant: FindMerchantPortOut
) {

    fun findBy(id: UUID): Either<ApplicationError, Merchant> =
        findMerchant.findBy(id)
            .logRight { info("merchant found: {}", it) }

    companion object : CompanionLogger()
}
