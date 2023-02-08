package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.shared.domain.Acquirer
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerBuilder {

    fun buildFrom(
        acquirers: List<AcquirerOperable>?,
        installments: List<AcquirerInstallment>?,
        tags: List<AcquirerTag>?
    ) =
        acquirers
            ?.takeIf { it.isNotEmpty() }
            ?.map { acquirer ->
                Acquirer(
                    acquirerId = acquirer.acquirerId,
                    code = acquirer.code,
                    installments = installments
                        ?.filter { it.acquirerId == acquirer.acquirerId }
                        ?.takeIf { it.isNotEmpty() }
                        ?.flatMap {
                            it.installments.map { installment ->
                                Acquirer.Installment(
                                    brand = installment.brand,
                                    number = installment.number
                                )
                            }
                        },
                    tags = tags
                        ?.filter { it.acquirerId == acquirer.acquirerId }
                        ?.takeIf { it.isNotEmpty() }
                        ?.flatMap { it.tags }
                )
            }.log { info("acquirers entities built: {}", it) }

    companion object : CompanionLogger()
}
