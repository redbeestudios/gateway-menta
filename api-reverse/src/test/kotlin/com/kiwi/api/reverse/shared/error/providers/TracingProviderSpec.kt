package com.kiwi.api.reverse.shared.error.providers

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContext
import org.springframework.cloud.sleuth.Tracer

class TracingProviderSpec : FeatureSpec({

    val tracer = mockk<Tracer>()
    val provider = TracingProvider(tracer)

    beforeEach {
        clearAllMocks()
    }

    feature("provide tracing") {

        scenario("tracing provided") {

            val span = mockk<Span>()
            val context = mockk<TraceContext>()
            val traceId = "Soy un traceId"
            val spanId = "Soy un spanId"

            //given
            every { tracer.currentSpan() } returns span
            every { span.context() } returns context
            every { context.traceId() } returns traceId
            every { context.spanId() } returns spanId

            //expect
            provider.provide() shouldBe Tracing(traceId, spanId)
            verify(exactly = 2) { tracer.currentSpan() }
            verify(exactly = 2) { span.context() }
            verify(exactly = 1) { context.traceId() }
            verify(exactly = 1) { context.spanId() }
        }

        scenario("tracing not provided") {

            //given
            every { tracer.currentSpan() } returns null

            //expect
            provider.provide() shouldBe Tracing("Trace id doesnt exist", "Span id doesnt exist")
            verify(exactly = 2) { tracer.currentSpan() }
        }

        scenario("context not provided") {

            val span = mockk<Span>()

            //given
            every { tracer.currentSpan() } returns span
            every { span.context() } returns null

            //expect
            provider.provide() shouldBe Tracing("Trace id doesnt exist", "Span id doesnt exist")
            verify(exactly = 2) { tracer.currentSpan() }
            verify(exactly = 2) { span.context() }
        }

        scenario("traceId not provided") {

            val span = mockk<Span>()
            val context = mockk<TraceContext>()

            //given
            every { tracer.currentSpan() } returns span
            every { span.context() } returns context
            every { context.traceId() } returns null
            every { context.spanId() } returns null

            //expect
            provider.provide() shouldBe Tracing("Trace id doesnt exist", "Span id doesnt exist")
            verify(exactly = 2) { tracer.currentSpan() }
            verify(exactly = 2) { span.context() }
            verify(exactly = 1) { context.traceId() }
            verify(exactly = 1) { context.spanId() }
        }
    }
})
