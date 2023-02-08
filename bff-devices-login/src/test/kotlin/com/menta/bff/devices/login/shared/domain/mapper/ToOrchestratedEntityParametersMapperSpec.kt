package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOrchestratedEntityParametersMapperSpec : FeatureSpec({

    val mapper = ToOrchestratedEntityParametersMapper()

    beforeEach { clearAllMocks() }

    feature("mapper OrchestratedEntitiesRequest to OrchestratedEntityParameters") {
        val terminal = aTerminal()
        scenario("with terminalSerialCode") {
            val request = OrchestratedEntitiesRequest(
                terminalSerialCode = terminal.serialCode,
                terminalModel = null
            )
            mapper.mapFrom(request) shouldBe OrchestratedEntityParameters(
                terminalSerialCode = terminal.serialCode,
                terminalModel = null
            )
        }
        scenario("without terminalSerialCode") {
            val request = OrchestratedEntitiesRequest(
                terminalSerialCode = null,
                terminalModel = null
            )
            mapper.mapFrom(request) shouldBe OrchestratedEntityParameters(
                terminalSerialCode = null,
                terminalModel = null
            )
        }
        scenario("with terminalModel") {
            val request = OrchestratedEntitiesRequest(
                terminalSerialCode = null,
                terminalModel = terminal.model
            )
            mapper.mapFrom(request) shouldBe OrchestratedEntityParameters(
                terminalSerialCode = null,
                terminalModel = terminal.model
            )
        }
        scenario("without terminalModel") {
            val request = OrchestratedEntitiesRequest(
                terminalSerialCode = null,
                terminalModel = null
            )
            mapper.mapFrom(request) shouldBe OrchestratedEntityParameters(
                terminalSerialCode = null,
                terminalModel = null
            )
        }
    }
})
