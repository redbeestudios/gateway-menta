package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.acquirers.aAcquirers
import com.menta.bff.devices.login.entities.acquirers.aAcquirersOperable
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.installments.aAcquirersInstallments
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.merchant.aTaxMerchant
import com.menta.bff.devices.login.entities.tags.anAcquirerTagsEmv
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOrchestratedEntitiesMapperSpec : FeatureSpec({

    val toAcquirerBuilder = mockk<ToAcquirerBuilder>()
    val toMerchantBuilder = mockk<ToMerchantBuilder>()
    val builder = ToOrchestratedEntitiesBuilder(toAcquirerBuilder, toMerchantBuilder)

    beforeEach { clearAllMocks() }

    feature("mapper to OrchestratedEntities") {
        scenario("from customer") {
            val customer = aCustomer()
            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = customer,
                merchant = null,
                taxMerchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = null
            ) shouldBe OrchestratedEntities(
                customer = customer,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
        scenario("from merchant") {
            val merchant = aMerchant()
            val taxMerchant = aTaxMerchant()
            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(merchant, taxMerchant) } returns merchant

            builder.buildFrom(
                customer = null,
                merchant = merchant,
                taxMerchant = taxMerchant,
                terminal = null,
                workflows = null,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = null
            ) shouldBe OrchestratedEntities(
                customer = null,
                merchant = merchant,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
        scenario("from terminal") {
            val terminal = aTerminal()
            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = null,
                merchant = null,
                taxMerchant = null,
                terminal = terminal,
                workflows = null,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = null
            ) shouldBe OrchestratedEntities(
                customer = null,
                merchant = null,
                terminal = terminal,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
        scenario("from workflow") {
            val workFlows = aWorkFlows()

            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = null,
                merchant = null,
                taxMerchant = null,
                terminal = null,
                workflows = workFlows,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = null
            ) shouldBe OrchestratedEntities(
                customer = null,
                merchant = null,
                terminal = null,
                workflows = workFlows,
                acquirers = null,
                deviceFlows = null
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
        scenario("from device flow") {
            val deviceFlows = anDevicesFlows()

            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = null,
                merchant = null,
                taxMerchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = deviceFlows
            ) shouldBe OrchestratedEntities(
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = deviceFlows
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
        scenario("from acquirers") {
            val acqOperable = aAcquirersOperable()
            val acqInstallments = listOf(aAcquirersInstallments())
            val acquirers = aAcquirers()
            val acqEmvs = anAcquirerTagsEmv()

            every { toAcquirerBuilder.buildFrom(acqOperable, acqInstallments, acqEmvs) } returns acquirers
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = null,
                merchant = null,
                taxMerchant = null,
                terminal = null,
                workflows = null,
                acquirers = acqOperable,
                installments = acqInstallments,
                tagsEmv = acqEmvs,
                deviceFlows = null
            ) shouldBe OrchestratedEntities(
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = acquirers,
                deviceFlows = null
            )
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(acqOperable, acqInstallments, acqEmvs) }
        }
        scenario("from all params null") {
            every { toAcquirerBuilder.buildFrom(null, null, null) } returns null
            every { toMerchantBuilder.buildFrom(null, null) } returns null

            builder.buildFrom(
                customer = null,
                merchant = null,
                taxMerchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                installments = null,
                tagsEmv = null,
                deviceFlows = null
            ) shouldBe null
            verify(exactly = 1) { toAcquirerBuilder.buildFrom(null, null, null) }
        }
    }
})
