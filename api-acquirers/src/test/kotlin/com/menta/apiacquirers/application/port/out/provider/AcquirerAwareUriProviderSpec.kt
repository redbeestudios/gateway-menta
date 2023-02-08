package com.menta.apiacquirers.application.port.out.provider

import arrow.core.left
import arrow.core.right
import com.menta.apiacquirers.adapter.out.AcquirerPathProvider
import com.menta.apiacquirers.adapter.out.OperationPathProvider
import com.menta.apiacquirers.adapter.out.provider.AcquirerAwareUriProvider
import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForAcquirer
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForOperation
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.util.UriComponentsBuilder

class AcquirerAwareUriProviderSpec : FeatureSpec({

    val host = "api-internal.dev.apps.menta.global"
    val scheme = "https"
    val acquirerPathProvider = mockk<AcquirerPathProvider>()
    val operationPathProvider = mockk<OperationPathProvider>()

    val provider = AcquirerAwareUriProvider(
        host = host,
        scheme = scheme,
        acquirerPathProvider = acquirerPathProvider,
        operationPathProvider = operationPathProvider
    )

    beforeEach { clearAllMocks() }

    feature("provide for acquirer and operation type") {

        scenario("uri provided") {
            every { acquirerPathProvider.provideBy("GPS") } returns "/gps".right()
            every { operationPathProvider.provideBy(PAYMENTS) } returns "/payments".right()

            provider.provideFor(OperableAcquirers.Acquirer("GPS", "ARG"), PAYMENTS) shouldBeRight
                UriComponentsBuilder.newInstance()
                    .scheme(scheme)
                    .host(host)
                    .path("/gps" + "/payments")
                    .build()
                    .toUri()
        }

        scenario("error searching for acquirer path") {
            every { acquirerPathProvider.provideBy("GPS") } returns missingPathForAcquirer("GPS").left()

            provider.provideFor(OperableAcquirers.Acquirer("GPS", "ARG"), PAYMENTS) shouldBeLeft
                missingPathForAcquirer("GPS")
        }

        scenario("error searching for operation path") {
            every { acquirerPathProvider.provideBy("GPS") } returns "/gps".right()
            every { operationPathProvider.provideBy(PAYMENTS) } returns missingPathForOperation(PAYMENTS).left()

            provider.provideFor(OperableAcquirers.Acquirer("GPS", "ARG"), PAYMENTS) shouldBeLeft
                missingPathForOperation(PAYMENTS)
        }
    }
})
