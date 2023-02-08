package com.menta.api.merchants.acquirer.adapter.out

import arrow.core.right
import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.acquirer.anAcquirerId
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class AcquirerMerchantRepositoryTest : FeatureSpec({

    val dbRepository = mockk<AcquirerMerchantDbRepository>()
    val repository = AcquirerMerchantRepository(dbRepository)

    beforeEach { clearAllMocks() }

    feature("find by acquirer merchant id") {

        scenario("merchant found") {

            every { dbRepository.findByAcquirerAndMerchantId(anAcquirerId, aMerchantId) } returns Optional.of(
                anAcquirerMerchant
            )

            repository.findBy(anAcquirerId, aMerchantId) shouldBe Optional.of(anAcquirerMerchant)

            verify(exactly = 1) { dbRepository.findByAcquirerAndMerchantId(anAcquirerId, aMerchantId) }
        }

        scenario("merchant NOT found") {

            every { dbRepository.findByAcquirerAndMerchantId(anAcquirerId, aMerchantId) } returns Optional.empty()

            repository.findBy(anAcquirerId, aMerchantId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByAcquirerAndMerchantId(anAcquirerId, aMerchantId) }
        }
    }

    feature("create") {
        scenario("acquirer merchant saved") {
            val newAcquirerMerchant = anAcquirerMerchant

            every { dbRepository.insert(newAcquirerMerchant) } returns anAcquirerMerchant

            repository.create(newAcquirerMerchant) shouldBe anAcquirerMerchant

            verify(exactly = 1) { dbRepository.insert(newAcquirerMerchant) }
        }
    }

    feature("update") {
        scenario("acquirer merchant updated") {
            val acquirerMerchant = anAcquirerMerchant

            every { dbRepository.save(acquirerMerchant) } returns anAcquirerMerchant

            repository.update(acquirerMerchant) shouldBe anAcquirerMerchant.right()

            verify(exactly = 1) { dbRepository.save(acquirerMerchant) }
        }
    }
})
