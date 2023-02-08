package com.menta.apiacquirers.application.port.out

import com.menta.apiacquirers.adapter.out.AcquirerPath
import com.menta.apiacquirers.adapter.out.AcquirerPathProvider
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForAcquirer
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class AcquirerPathSpec : FeatureSpec({

    feature("provide an acquirer path") {

        scenario("path provided") {
            val gps = AcquirerPath("GPS", "/gps")
            val feenicia = AcquirerPath("FEENICIA", "/feenicia")
            val credibanco = AcquirerPath("CREDIBANCO", "/credibanco")
            val acquirerPathProvider = AcquirerPathProvider(listOf(gps, feenicia, credibanco))

            acquirerPathProvider.provideBy("GPS") shouldBeRight "/gps"
            acquirerPathProvider.provideBy("FEENICIA") shouldBeRight "/feenicia"
            acquirerPathProvider.provideBy("CREDIBANCO") shouldBeRight "/credibanco"
        }

        scenario("path not provided") {

            val feenicia = AcquirerPath("FEENICIA", "/feenicia")
            val acquirerPathProvider = AcquirerPathProvider(listOf(feenicia))

            acquirerPathProvider.provideBy("GPS") shouldBeLeft missingPathForAcquirer("GPS")
        }
    }
})
