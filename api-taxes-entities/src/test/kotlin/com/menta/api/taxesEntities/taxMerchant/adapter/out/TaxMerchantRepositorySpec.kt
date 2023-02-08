package com.menta.api.taxesEntities.taxMerchant.adapter.out

import arrow.core.right
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.adapter.out.TaxMerchantDbRepository
import com.menta.api.taxesEntities.adapter.out.TaxMerchantRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class TaxMerchantRepositorySpec : FeatureSpec({

    val dbRepository = mockk<TaxMerchantDbRepository>()
    val repository = TaxMerchantRepository(dbRepository)

    beforeEach { clearAllMocks() }

    feature("find tax merchant by id") {

        scenario("tax merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchant = aTaxMerchant()

            every {
                dbRepository.findByMerchantId(merchantId)
            } returns Optional.of(merchant)

            repository.findBy(merchantId) shouldBe Optional.of(merchant)

            verify(exactly = 1) { dbRepository.findByMerchantId(merchantId) }
        }

        scenario("tax merchant NOT found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            every {
                dbRepository.findByMerchantId(merchantId)
            } returns Optional.empty()

            repository.findBy(merchantId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByMerchantId(merchantId) }
        }
    }

    feature("create") {
        scenario("tax merchant create") {
            val merchant = aTaxMerchant()

            every { dbRepository.insert(merchant) } returns aTaxMerchant()

            repository.create(merchant) shouldBe aTaxMerchant().right()
        }
    }

    feature("update tax merchant") {
        scenario("OK") {
            val merchant = aTaxMerchant()

            every { dbRepository.save(merchant) } returns aTaxMerchant()

            repository.update(merchant) shouldBe aTaxMerchant().right()

            verify(exactly = 1) { dbRepository.save(merchant) }
        }
    }
})
