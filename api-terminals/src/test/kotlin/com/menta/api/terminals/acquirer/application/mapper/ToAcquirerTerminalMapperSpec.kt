package com.menta.api.terminals.acquirer.application.mapper

import com.menta.api.terminals.aPreAcquirerTerminal
import com.menta.api.terminals.acquirer.anAcquirerTerminal
import com.menta.api.terminals.applications.mapper.ToAcquirerTerminalMapper
import com.menta.api.terminals.domain.provider.IdProvider
import com.menta.api.terminals.acquireId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToAcquirerTerminalMapperSpec : FeatureSpec({

    lateinit var idProvider: IdProvider

    feature("map create acquirer terminal") {

        idProvider = mockk()
        val mapper = ToAcquirerTerminalMapper(idProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val preAcquirerTerminal = aPreAcquirerTerminal
            val acquirerTerminal = anAcquirerTerminal

            every { idProvider.provide() } returns acquireId

            mapper.map(preAcquirerTerminal) shouldBe acquirerTerminal
        }
    }
})
