package com.menta.apisecrets.adapter.out.repository

import arrow.core.right
import com.menta.apisecrets.adapter.out.AcquirerRepository
import com.menta.apisecrets.adapter.out.model.OperableAcquirer
import com.menta.apisecrets.domain.Acquirer.GPS
import com.menta.apisecrets.domain.Country.ARG
import com.menta.apisecrets.domain.Country.MEX
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class AcquirerRepositorySpec : FeatureSpec({

    beforeEach { clearAllMocks() }

    feature("find acquirer") {
        scenario("acquirer found") {
            val operableAcquirer =
                OperableAcquirer(acquirers = listOf(OperableAcquirer.AcquirerConfig(GPS, listOf(ARG))))
            val repository = AcquirerRepository(operableAcquirer)
            repository.findBy(ARG) shouldBe GPS.right()
        }

        scenario("acquirer not found") {
            val operableAcquirer =
                OperableAcquirer(acquirers = listOf(OperableAcquirer.AcquirerConfig(GPS, listOf(ARG))))
            val repository = AcquirerRepository(operableAcquirer)

            repository.findBy(MEX).isLeft() shouldBe true
        }
    }
})
