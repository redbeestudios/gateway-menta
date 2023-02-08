package com.menta.bff.devices.login.orchestrate.adapter.`in`.model

import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.shared.domain.Acquirer
import com.menta.bff.devices.login.shared.domain.UserAuth
import io.swagger.v3.oas.annotations.media.Schema

data class OrchestratorResponse(
    @Schema(description = "response from the login")
    val auth: UserAuth,
    @Schema(description = "present if user is a customer")
    val customer: Customer?,
    @Schema(description = "present if user is a merchant")
    val merchant: Merchant?,
    @Schema(description = "present if serialCode is informed in the request")
    val terminal: Terminal?,
    @Schema(description = "present if user has allowed workflows")
    val workflows: List<WorkFlow>?,
    @Schema(description = "present if user has operable acquirers")
    val acquirers: List<Acquirer>?,
    @Schema(description = "present if terminalModel is informed in the request")
    val deviceFlows: List<DeviceFlow>?
)
