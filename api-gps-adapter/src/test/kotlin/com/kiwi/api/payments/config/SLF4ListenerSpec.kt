package com.kiwi.api.payments.config

import com.kiwi.api.payments.adapter.controller.PaymentController.Companion.log
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jpos.util.LogEvent

class SLF4ListenerSpec : FeatureSpec({

    feature("SLF4Listener for jpos") {

        lateinit var slf4Listener: SLF4JListener

        lateinit var event: LogEvent

        beforeEach {
            event = mockk()

            slf4Listener = SLF4JListener()
        }

        scenario("realm with channel/ must be logged") {
            every { event.realm } returns "channel/"

            slf4Listener.log(event) shouldBe event

            verify(exactly = 1) { event.log { info(it.toString()) } }
        }

        scenario("realm without channel/ must not be logged") {
            every { event.realm } returns "otherRealm"

            slf4Listener.log(event) shouldBe event

            verify(exactly = 0) { event.log { info(it.toString()) } }
        }

        scenario("null realm must not be logged") {
            every { event.realm } returns null

            slf4Listener.log(event) shouldBe event

            verify(exactly = 0) { event.log { info(it.toString()) } }
        }
    }
})
