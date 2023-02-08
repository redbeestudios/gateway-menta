package com.menta.bff.devices.login.orchestrate.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.acquirers.application.service.FindAcquirersOperableApplicationService
import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.entities.customer.application.service.FindCustomerApplicationService
import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.entities.deviceFlow.application.service.FindDeviceFlowApplicationService
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.entities.installments.application.service.FindAcquirersInstallmentsApplicationService
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.entities.merchant.application.service.FindMerchantApplicationService
import com.menta.bff.devices.login.entities.merchant.application.service.FindTaxMerchantApplicationService
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.entities.tags.application.service.FindAcquirerTagApplicationService
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.entities.terminals.application.service.FindTerminalApplicationService
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.entities.workflow.application.service.FindWorkFlowsApplicationService
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.domain.UserType.CUSTOMER
import com.menta.bff.devices.login.shared.domain.builder.ToOrchestratedEntitiesBuilder
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrchestrateEntitiesService(
    private val findUser: UserApplicationService,
    private val findCustomer: FindCustomerApplicationService,
    private val findMerchant: FindMerchantApplicationService,
    private val findTaxMerchant: FindTaxMerchantApplicationService,
    private val findTerminal: FindTerminalApplicationService,
    private val findWorkFlows: FindWorkFlowsApplicationService,
    private val findAcquirers: FindAcquirersOperableApplicationService,
    private val findInstallments: FindAcquirersInstallmentsApplicationService,
    private val findTags: FindAcquirerTagApplicationService,
    private val findDeviceFlow: FindDeviceFlowApplicationService,
    private val toOrchestratedEntitiesBuilder: ToOrchestratedEntitiesBuilder
) {

    fun orchestrate(
        email: Email,
        type: UserType,
        userAuth: UserAuth,
        entityParameters: OrchestratedEntityParameters?
    ): Either<ApplicationError, OrchestratedEntities?> =
        findUser(email, type).map { user ->
            runBlocking {
                buildEntities(
                    terminal = withContext(MDCContext()) { entityParameters?.findTerminal() },
                    customer = withContext(MDCContext()) { user.attributes.customerId?.let { findCustomerFor(it, type) } },
                    merchant = withContext(MDCContext()) { user.attributes.merchantId?.let { findMerchant(it) } },
                    taxMerchant = withContext(MDCContext()) { user.attributes.merchantId?.let { findTaxMerchant(it) } },
                    workflows = withContext(MDCContext()) { findWorkFlows(email, type, userAuth) },
                    acquirers = withContext(MDCContext()) { user.attributes.customerId?.let { findAcquirersOperableFor(it, userAuth) } },
                    installments = withContext(MDCContext()) { user.attributes.merchantId?.let { findAcquirersInstallments(it, userAuth) } },
                    tagsEmv = withContext(MDCContext()) { user.attributes.customerId?.let { findAcquirersTags(it, userAuth) } },
                    deviceFlows = withContext(MDCContext()) { entityParameters?.findDeviceFlows(userAuth) },
                )
            }
        }

    private fun findUser(email: Email, type: UserType): Either<ApplicationError, User> =
        findUser.find(email, type)
            .logRight { info("user found: {}", it) }

    private suspend fun OrchestratedEntityParameters.findTerminal() =
        terminalSerialCode?.let { findTerminal.findBy(it).orNull() }
            .log { info("terminal search result: {}", it) }

    private suspend fun OrchestratedEntityParameters.findDeviceFlows(userAuth: UserAuth) =
        terminalModel?.let { findDeviceFlow.findBy(it, userAuth).orNull() }
            .log { info("device flows search result: {}", it) }

    private suspend fun findCustomerFor(customerId: UUID, type: UserType) =
        if (type == CUSTOMER) {
            findCustomer.findBy(customerId).orNull()
                .log { info("customer search result: {}", it) }
        } else {
            null
        }

    private suspend fun findMerchant(merchantId: UUID) =
        findMerchant.findBy(merchantId).orNull()
            .log { info("merchant search result: {}", it) }

    private suspend fun findTaxMerchant(merchantId: UUID) =
        findTaxMerchant.findBy(merchantId).orNull()
            .log { info("tax merchant search result: {}", it) }

    private suspend fun findWorkFlows(email: String, type: UserType, userAuth: UserAuth) =
        findWorkFlows.find(email, type, userAuth).orNull()
            .log { info("workflows search result: {}", it) }

    private suspend fun findAcquirersOperableFor(customerId: UUID, userAuth: UserAuth) =
        findAcquirers.findBy(customerId, userAuth).orNull()
            .log { info("acquirers operable search result: {}", it) }

    private suspend fun findAcquirersInstallments(merchantId: UUID, userAuth: UserAuth) =
        findInstallments.findBy(merchantId, userAuth).orNull()
            .log { info("acquirers installments search result: {}", it) }

    private suspend fun findAcquirersTags(customerId: UUID, userAuth: UserAuth) =
        findTags.findTagEmvBy(customerId, userAuth).orNull()
            .log { info("acquirers tags search result: {}", it) }

    private fun buildEntities(
        terminal: Terminal?,
        customer: Customer?,
        merchant: Merchant?,
        taxMerchant: TaxMerchant?,
        workflows: List<WorkFlow>?,
        acquirers: List<AcquirerOperable>?,
        installments: List<AcquirerInstallment>?,
        tagsEmv: List<AcquirerTag>?,
        deviceFlows: List<DeviceFlow>?
    ) = toOrchestratedEntitiesBuilder.buildFrom(
        terminal,
        customer,
        merchant,
        taxMerchant,
        workflows,
        acquirers,
        installments,
        tagsEmv,
        deviceFlows
    )
        .log { info("orchestrated entities build: {}", it) }

    companion object : CompanionLogger()
}
