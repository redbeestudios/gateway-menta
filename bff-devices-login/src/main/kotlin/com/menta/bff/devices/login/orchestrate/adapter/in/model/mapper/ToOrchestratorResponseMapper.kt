package com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper

import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.entities.workflow.domain.WorkFlow
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratorResponse
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.shared.domain.Acquirer
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratorResponseMapper {
    fun mapFrom(orchestratedAuth: OrchestratedAuth): OrchestratorResponse =
        with(orchestratedAuth) {
            OrchestratorResponse(
                auth = userAuth,
                customer = orchestratedEntities?.customer,
                merchant = orchestratedEntities?.merchant,
                terminal = orchestratedEntities?.terminal,
                workflows = orchestratedEntities?.workflows?.mapWorkFlowsIsNotEmpty(),
                acquirers = orchestratedEntities?.acquirers?.mapAcquirersIsNotEmpty(),
                deviceFlows = orchestratedEntities?.deviceFlows?.mapDeviceFlowsIsNotEmpty()
            )
        }.log { info("response mapped: {}", it) }

    private fun List<WorkFlow>.mapWorkFlowsIsNotEmpty() =
        takeIf { this.isNotEmpty() }?.let { this }

    private fun List<Acquirer>.mapAcquirersIsNotEmpty() =
        takeIf { this.isNotEmpty() }?.let { this }

    private fun List<DeviceFlow>.mapDeviceFlowsIsNotEmpty() =
        takeIf { this.isNotEmpty() }?.let { this }

    companion object : CompanionLogger()
}
