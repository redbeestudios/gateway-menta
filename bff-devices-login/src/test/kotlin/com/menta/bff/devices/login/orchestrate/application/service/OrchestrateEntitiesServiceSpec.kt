package com.menta.bff.devices.login.orchestrate.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.acquirers.aAcquirers
import com.menta.bff.devices.login.entities.acquirers.aAcquirersOperable
import com.menta.bff.devices.login.entities.acquirers.application.service.FindAcquirersOperableApplicationService
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.customer.application.service.FindCustomerApplicationService
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.deviceFlow.application.service.FindDeviceFlowApplicationService
import com.menta.bff.devices.login.entities.installments.aAcquirersInstallments
import com.menta.bff.devices.login.entities.installments.application.service.FindAcquirersInstallmentsApplicationService
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.merchant.aTaxMerchant
import com.menta.bff.devices.login.entities.merchant.application.service.FindMerchantApplicationService
import com.menta.bff.devices.login.entities.merchant.application.service.FindTaxMerchantApplicationService
import com.menta.bff.devices.login.entities.tags.anAcquirerTagsEmv
import com.menta.bff.devices.login.entities.tags.application.service.FindAcquirerTagApplicationService
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.terminals.application.service.FindTerminalApplicationService
import com.menta.bff.devices.login.entities.user.aCustomerUser
import com.menta.bff.devices.login.entities.user.aMerchantUser
import com.menta.bff.devices.login.entities.user.aSupportUser
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.entities.workflow.application.service.FindWorkFlowsApplicationService
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.builder.ToOrchestratedEntitiesBuilder
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestrateEntitiesServiceSpec : FeatureSpec({

    val findUser = mockk<UserApplicationService>()
    val findCustomer = mockk<FindCustomerApplicationService>()
    val findMerchant = mockk<FindMerchantApplicationService>()
    val findTaxMerchant = mockk<FindTaxMerchantApplicationService>()
    val findTerminal = mockk<FindTerminalApplicationService>()
    val findWorkFlows = mockk<FindWorkFlowsApplicationService>()
    val findAcquirers = mockk<FindAcquirersOperableApplicationService>()
    val findInstallments = mockk<FindAcquirersInstallmentsApplicationService>()
    val findTags = mockk<FindAcquirerTagApplicationService>()
    val findDeviceFlow = mockk<FindDeviceFlowApplicationService>()
    val toOrchestratedEntitiesBuilder = mockk<ToOrchestratedEntitiesBuilder>()

    val service = OrchestrateEntitiesService(
        findUser = findUser,
        findCustomer = findCustomer,
        findMerchant = findMerchant,
        findTaxMerchant = findTaxMerchant,
        findTerminal = findTerminal,
        findWorkFlows = findWorkFlows,
        findAcquirers = findAcquirers,
        findInstallments = findInstallments,
        findTags = findTags,
        findDeviceFlow = findDeviceFlow,
        toOrchestratedEntitiesBuilder = toOrchestratedEntitiesBuilder
    )

    beforeEach { clearAllMocks() }

    feature("orchestrate") {
        val merchant = aMerchant()
        val taxMerchant = aTaxMerchant()
        val workFlows = aWorkFlows()
        val userAuth = aUserAuthResponseWithToken()
        val acqOperable = aAcquirersOperable()
        val acqInstallments = listOf(aAcquirersInstallments())
        val acquirers = aAcquirers()
        val acqEmvs = anAcquirerTagsEmv()
        val customer = aCustomer()
        val terminal = aTerminal()
        val deviceFlows = anDevicesFlows()

        scenario("user not found") {
            val user = aMerchantUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val error = notFound("user $email for $type not found")

            every { findUser.find(email, type) } returns error.left()

            service.orchestrate(email, type, userAuth, null) shouldBeLeft error

            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 0) { findCustomer.findBy(any()) }
            verify(exactly = 0) { findMerchant.findBy(any()) }
            verify(exactly = 0) { findTerminal.findBy(any()) }
            verify(exactly = 0) { findAcquirers.findBy(any(), any()) }
            verify(exactly = 0) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }
        scenario("merchant user logged in, merchant found") {
            val user = aMerchantUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val orchestratedEntities =
                OrchestratedEntities(
                    terminal = null,
                    merchant = merchant,
                    customer = null,
                    workflows = workFlows,
                    acquirers = acquirers,
                    deviceFlows = null
                )

            every { findUser.find(email, type) } returns user.right()
            every { findMerchant.findBy(user.attributes.merchantId!!) } returns merchant.right()
            every { findTaxMerchant.findBy(user.attributes.merchantId!!) } returns taxMerchant.right()
            every { findWorkFlows.find(email, type, userAuth) } returns workFlows.right()
            every { findAcquirers.findBy(user.attributes.customerId!!, userAuth) } returns acqOperable.right()
            every { findInstallments.findBy(user.attributes.merchantId!!, userAuth) } returns acqInstallments.right()
            every { findTags.findTagEmvBy(user.attributes.customerId!!, userAuth) } returns acqEmvs.right()
            every {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = merchant,
                    taxMerchant = taxMerchant,
                    workflows = workFlows,
                    acquirers = acqOperable,
                    installments = acqInstallments,
                    tagsEmv = acqEmvs,
                    deviceFlows = null
                )
            } returns orchestratedEntities

            service.orchestrate(email, type, userAuth, null) shouldBeRight orchestratedEntities

            verify(exactly = 0) { findCustomer.findBy(any()) }
            verify(exactly = 0) { findTerminal.findBy(any()) }
            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 1) { findMerchant.findBy(user.attributes.merchantId!!) }
            verify(exactly = 1) { findAcquirers.findBy(user.attributes.customerId!!, userAuth) }
            verify(exactly = 1) { findInstallments.findBy(user.attributes.merchantId!!, userAuth) }
            verify(exactly = 1) { findTags.findTagEmvBy(user.attributes.customerId!!, userAuth) }
            verify(exactly = 1) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = merchant,
                    taxMerchant = taxMerchant,
                    workflows = workFlows,
                    acquirers = acqOperable,
                    installments = acqInstallments,
                    tagsEmv = acqEmvs,
                    deviceFlows = null
                )
            }
        }
        scenario("customer user logged in, customer found") {
            val user = aCustomerUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val orchestratedEntities =
                OrchestratedEntities(
                    terminal = null,
                    merchant = null,
                    customer = customer,
                    workflows = workFlows,
                    acquirers = acquirers,
                    deviceFlows = null
                )

            every { findUser.find(email, type) } returns user.right()
            every { findCustomer.findBy(user.attributes.customerId!!) } returns customer.right()
            every { findWorkFlows.find(email, type, userAuth) } returns workFlows.right()
            every { findAcquirers.findBy(user.attributes.customerId!!, userAuth) } returns acqOperable.right()
            every { findTags.findTagEmvBy(user.attributes.customerId!!, userAuth) } returns acqEmvs.right()
            every {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = customer,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = acqOperable,
                    installments = null,
                    tagsEmv = acqEmvs,
                    deviceFlows = null
                )
            } returns orchestratedEntities

            service.orchestrate(email, type, userAuth, null) shouldBeRight orchestratedEntities

            verify(exactly = 0) { findTerminal.findBy(any()) }
            verify(exactly = 0) { findMerchant.findBy(any()) }
            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 1) { findCustomer.findBy(user.attributes.customerId!!) }
            verify(exactly = 1) { findAcquirers.findBy(user.attributes.customerId!!, userAuth) }
            verify(exactly = 1) { findTags.findTagEmvBy(user.attributes.customerId!!, userAuth) }
            verify(exactly = 1) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = customer,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = acqOperable,
                    installments = null,
                    tagsEmv = acqEmvs,
                    deviceFlows = null
                )
            }
        }
        scenario("support user logged in, support found") {
            val user = aSupportUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val orchestratedEntities = OrchestratedEntities(null, null, null, workFlows, null, null)

            every { findUser.find(email, type) } returns user.right()
            every { findWorkFlows.find(email, type, userAuth) } returns workFlows.right()
            every { findDeviceFlow.findBy(terminal.model, userAuth) } returns deviceFlows.right()
            every {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = null
                )
            } returns orchestratedEntities

            service.orchestrate(email, type, userAuth, null) shouldBeRight orchestratedEntities

            verify(exactly = 0) { findTerminal.findBy(any()) }
            verify(exactly = 0) { findMerchant.findBy(any()) }
            verify(exactly = 0) { findCustomer.findBy(any()) }
            verify(exactly = 0) { findAcquirers.findBy(any(), any()) }
            verify(exactly = 0) { findInstallments.findBy(any(), any()) }
            verify(exactly = 0) { findTags.findTagEmvBy(any(), any()) }
            verify(exactly = 0) { findDeviceFlow.findBy(any(), any()) }
            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 1) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = null
                )
            }
        }
        scenario("support user logged in with entities parameters, support and terminal found") {
            val user = aSupportUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val parameters =
                OrchestratedEntityParameters(terminalSerialCode = terminal.serialCode, terminalModel = terminal.model)
            val orchestratedEntities = OrchestratedEntities(null, null, null, workFlows, null, deviceFlows)

            every { findUser.find(email, type) } returns user.right()
            every { findTerminal.findBy(terminal.serialCode) } returns terminal.right()
            every { findDeviceFlow.findBy(terminal.model, userAuth) } returns deviceFlows.right()
            every { findWorkFlows.find(email, type, userAuth) } returns workFlows.right()
            every {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = terminal,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = deviceFlows
                )
            } returns orchestratedEntities

            service.orchestrate(email, type, userAuth, parameters) shouldBeRight orchestratedEntities

            verify(exactly = 0) { findMerchant.findBy(any()) }
            verify(exactly = 0) { findCustomer.findBy(any()) }
            verify(exactly = 0) { findAcquirers.findBy(any(), any()) }
            verify(exactly = 0) { findInstallments.findBy(any(), any()) }
            verify(exactly = 0) { findTags.findTagEmvBy(any(), any()) }
            verify(exactly = 1) { findDeviceFlow.findBy(terminal.model, userAuth) }
            verify(exactly = 1) { findTerminal.findBy(terminal.serialCode) }
            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 1) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = terminal,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = deviceFlows
                )
            }
        }
        scenario("support user logged in with entities parameters, terminal not found") {
            val user = aSupportUser()
            val type = user.attributes.type
            val email = user.attributes.email
            val error = notFound("terminal ${terminal.serialCode} not found")
            val orchestratedEntities = OrchestratedEntities(null, null, null, workFlows, null, null)
            val parameters =
                OrchestratedEntityParameters(terminalSerialCode = terminal.serialCode, terminalModel = null)

            every { findUser.find(email, type) } returns user.right()
            every { findTerminal.findBy(terminal.serialCode) } returns error.left()
            every { findWorkFlows.find(email, type, userAuth) } returns workFlows.right()
            every {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = null
                )
            } returns orchestratedEntities

            service.orchestrate(email, type, userAuth, parameters) shouldBeRight orchestratedEntities

            verify(exactly = 0) { findMerchant.findBy(any()) }
            verify(exactly = 0) { findCustomer.findBy(any()) }
            verify(exactly = 0) { findAcquirers.findBy(any(), any()) }
            verify(exactly = 0) { findInstallments.findBy(any(), any()) }
            verify(exactly = 0) { findTags.findTagEmvBy(any(), any()) }
            verify(exactly = 1) { findTerminal.findBy(terminal.serialCode) }
            verify(exactly = 1) { findUser.find(email, type) }
            verify(exactly = 1) {
                toOrchestratedEntitiesBuilder.buildFrom(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    taxMerchant = null,
                    workflows = workFlows,
                    acquirers = null,
                    installments = null,
                    tagsEmv = null,
                    deviceFlows = null
                )
            }
        }
    }
})
