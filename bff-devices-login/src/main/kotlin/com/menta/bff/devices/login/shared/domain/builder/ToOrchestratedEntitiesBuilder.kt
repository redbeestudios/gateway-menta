package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedEntitiesBuilder(
    private val toAcquirerBuilder: ToAcquirerBuilder,
    private val toMerchantBuilder: ToMerchantBuilder
) {

    fun buildFrom(
        terminal: Terminal?,
        customer: Customer?,
        merchant: Merchant?,
        taxMerchant: TaxMerchant?,
        workflows: List<WorkFlow>?,
        acquirers: List<AcquirerOperable>?,
        installments: List<AcquirerInstallment>?,
        tagsEmv: List<AcquirerTag>?,
        deviceFlows: List<DeviceFlow>?
    ) = OrchestratedEntities(
        terminal = terminal,
        merchant = toMerchantBuilder.buildFrom(merchant, taxMerchant),
        customer = customer,
        workflows = workflows,
        acquirers = toAcquirerBuilder.buildFrom(acquirers, installments, tagsEmv),
        deviceFlows = deviceFlows
    ).nullIfAllNull()
        .log { info("orchestrated entities built: {}", it) }

    private fun OrchestratedEntities.nullIfAllNull() =
        if (terminal == null && merchant == null && customer == null && workflows == null && acquirers == null && deviceFlows == null) {
            null
        } else {
            this
        }

    companion object : CompanionLogger()
}
