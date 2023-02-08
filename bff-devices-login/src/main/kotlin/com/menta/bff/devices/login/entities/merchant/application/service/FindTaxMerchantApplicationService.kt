package com.menta.bff.devices.login.entities.merchant.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.merchant.application.out.FindTaxMerchantPortOut
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindTaxMerchantApplicationService(
    private val findTaxMerchant: FindTaxMerchantPortOut
) {

    fun findBy(merchantId: UUID): Either<ApplicationError, TaxMerchant> =
        findTaxMerchant.findBy(merchantId)
            .logRight { info("tax merchant found: {}", it) }

    companion object : CompanionLogger()
}
