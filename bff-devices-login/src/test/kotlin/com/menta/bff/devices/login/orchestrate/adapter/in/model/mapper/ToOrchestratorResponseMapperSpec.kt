package com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper

import com.menta.bff.devices.login.entities.acquirers.aAcquirers
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.login.aUserAuthResponseWithChallenge
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratorResponse
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToOrchestratorResponseMapperSpec : FeatureSpec({

    val mapper = ToOrchestratorResponseMapper()

    feature("map from orchestratedAuth") {
        val terminal = aTerminal()
        val customer = aCustomer()
        val merchant = aMerchant()
        val workflows = aWorkFlows()
        val acquirers = aAcquirers()
        val deviceFlows = anDevicesFlows()

        scenario("auth with token and all entities") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = terminal,
                    customer = customer,
                    merchant = merchant,
                    workflows = workflows,
                    acquirers = acquirers,
                    deviceFlows = deviceFlows
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = orchestratedAuth.orchestratedEntities!!.customer,
                merchant = orchestratedAuth.orchestratedEntities!!.merchant,
                terminal = orchestratedAuth.orchestratedEntities!!.terminal,
                workflows = orchestratedAuth.orchestratedEntities!!.workflows,
                acquirers = orchestratedAuth.orchestratedEntities!!.acquirers,
                deviceFlows = orchestratedAuth.orchestratedEntities!!.deviceFlows
            )
        }
        scenario("auth with challenge and all entities") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithChallenge(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = terminal,
                    customer = customer,
                    merchant = merchant,
                    workflows = workflows,
                    acquirers = acquirers,
                    deviceFlows = deviceFlows
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = orchestratedAuth.orchestratedEntities!!.customer,
                merchant = orchestratedAuth.orchestratedEntities!!.merchant,
                terminal = orchestratedAuth.orchestratedEntities!!.terminal,
                workflows = orchestratedAuth.orchestratedEntities!!.workflows,
                acquirers = orchestratedAuth.orchestratedEntities!!.acquirers,
                deviceFlows = orchestratedAuth.orchestratedEntities!!.deviceFlows
            )
        }
        scenario("auth with token and no entities") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = null
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and some entities") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = terminal,
                    customer = null,
                    merchant = null,
                    workflows = workflows,
                    acquirers = null,
                    deviceFlows = null
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = orchestratedAuth.orchestratedEntities!!.terminal,
                workflows = orchestratedAuth.orchestratedEntities!!.workflows,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and workflows empty") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = listOf(),
                    acquirers = null,
                    deviceFlows = null
                )

            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and workflows null") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = null,
                    acquirers = null,
                    deviceFlows = null
                )

            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and acquirers empty") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = null,
                    acquirers = listOf(),
                    deviceFlows = null
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and acquirers null") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = null,
                    acquirers = null,
                    deviceFlows = null
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and device flows empty") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = null,
                    acquirers = null,
                    deviceFlows = listOf()
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
        scenario("auth with token and device flow null") {
            val orchestratedAuth = OrchestratedAuth(
                userAuth = aUserAuthResponseWithToken(),
                orchestratedEntities = OrchestratedEntities(
                    terminal = null,
                    customer = null,
                    merchant = null,
                    workflows = null,
                    acquirers = null,
                    deviceFlows = null
                )
            )

            mapper.mapFrom(orchestratedAuth) shouldBe OrchestratorResponse(
                auth = orchestratedAuth.userAuth,
                customer = null,
                merchant = null,
                terminal = null,
                workflows = null,
                acquirers = null,
                deviceFlows = null
            )
        }
    }
})
