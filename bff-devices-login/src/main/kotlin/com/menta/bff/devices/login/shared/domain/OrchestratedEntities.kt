package com.menta.bff.devices.login.shared.domain

import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow

data class OrchestratedEntities(
    val terminal: Terminal?,
    val merchant: Merchant?,
    val customer: Customer?,
    val workflows: List<WorkFlow>?,
    val acquirers: List<Acquirer>?,
    val deviceFlows: List<DeviceFlow>?
)
