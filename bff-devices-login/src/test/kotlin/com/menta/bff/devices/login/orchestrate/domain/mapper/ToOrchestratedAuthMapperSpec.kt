package com.menta.bff.devices.login.orchestrate.domain.mapper

import com.menta.bff.devices.login.entities.acquirers.aAcquirers
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.login.aUserAuthResponseWithChallenge
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToOrchestratedAuthMapperSpec : FeatureSpec({

    val mapper = ToOrchestratedAuthMapper()

    feature("mapper orchestrated to OrchestratedAuth") {
        val customer = aCustomer()
        val merchant = aMerchant()
        val terminal = aTerminal()
        val workflows = aWorkFlows()
        val acquirers = aAcquirers()
        val deviceFlows = anDevicesFlows()

        scenario("from userAuth") {
            val userAuth = aUserAuthResponseWithToken()

            mapper.mapFrom(userAuth, null) shouldBe
                OrchestratedAuth(
                    userAuth = userAuth,
                    orchestratedEntities = null
                )
        }

        scenario("from userAuth and customer and workflows") {
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedEntities =
                OrchestratedEntities(
                    customer = customer,
                    terminal = null,
                    merchant = null,
                    workflows = workflows,
                    acquirers = null,
                    deviceFlows = deviceFlows
                )

            mapper.mapFrom(userAuth = userAuth, orchestratedEntities) shouldBe OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = orchestratedEntities
            )
        }
        scenario("from userAuth and customer and merchant and workflows") {
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedEntities =
                OrchestratedEntities(
                    customer = customer,
                    terminal = null,
                    merchant = merchant,
                    workflows = workflows,
                    acquirers = null,
                    deviceFlows = null
                )

            mapper.mapFrom(userAuth, orchestratedEntities) shouldBe OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = orchestratedEntities
            )
        }
        scenario("from userAuth and terminal and workflows and device flows") {
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedEntities =
                OrchestratedEntities(
                    customer = null,
                    terminal = terminal,
                    merchant = null,
                    workflows = workflows,
                    acquirers = null,
                    deviceFlows = deviceFlows
                )

            mapper.mapFrom(userAuth = userAuth, orchestratedEntities) shouldBe OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = orchestratedEntities
            )
        }
        scenario("from userAuth and customer, merchant, terminal, workflows and acquirers") {
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedEntities =
                OrchestratedEntities(
                    customer = customer,
                    terminal = terminal,
                    merchant = merchant,
                    workflows = workflows,
                    acquirers = acquirers,
                    deviceFlows = null
                )

            mapper.mapFrom(userAuth, orchestratedEntities) shouldBe OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = orchestratedEntities
            )
        }
        scenario("from userAuth with challenge") {
            val userAuth = aUserAuthResponseWithChallenge()
            val orchestratedEntities = OrchestratedEntities(
                customer = customer,
                terminal = terminal,
                merchant = merchant,
                workflows = workflows,
                acquirers = acquirers,
                deviceFlows = deviceFlows
            )

            mapper.mapFrom(userAuth = userAuth, orchestratedEntities) shouldBe OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = orchestratedEntities
            )
        }
    }
})
