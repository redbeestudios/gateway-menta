package com.menta.api.merchants.merchant.adapter.out

import arrow.core.right
import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.aMerchantCreated
import com.menta.api.merchants.aMerchantQuery
import com.menta.api.merchants.adapter.out.MerchantDbRepository
import com.menta.api.merchants.adapter.out.MerchantRepository
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.Status.ACTIVE
import com.menta.api.merchants.domain.provider.DateProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.util.Optional
import java.util.UUID

class MerchantRepositoryTest : FeatureSpec({

    val dbRepository = mockk<MerchantDbRepository>()
    val mongoTemplate = mockk<MongoTemplate>(relaxed = true)
    lateinit var dateProvider: DateProvider


    beforeEach { clearAllMocks() }

    feature("find merchant by id") {
        dateProvider = mockk()
        val repository = MerchantRepository(dbRepository, mongoTemplate, dateProvider)

        scenario("merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchant = aMerchant()

            every {
                dbRepository.findByIdAndDeleteDateIsNull(merchantId)
            } returns Optional.of(merchant)

            repository.findBy(merchantId) shouldBe Optional.of(merchant)

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(merchantId) }
        }

        scenario("merchant NOT found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            every {
                dbRepository.findByIdAndDeleteDateIsNull(merchantId)
            } returns Optional.empty()

            repository.findBy(merchantId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(merchantId) }
        }
    }

    feature("update") {
        dateProvider = mockk()
        val repository = MerchantRepository(dbRepository, mongoTemplate, dateProvider)

        scenario("merchant updated") {
            val merchant = aMerchant()

            every { dbRepository.save(merchant) } returns aMerchant()

            repository.update(merchant) shouldBe aMerchant().right()

            verify(exactly = 1) { dbRepository.save(merchant) }
        }
    }

    feature("find by tax") {
        dateProvider = mockk()
        val repository = MerchantRepository(dbRepository, mongoTemplate, dateProvider)

        scenario("merchant found") {
            val merchant = aMerchantCreated()

            every {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    merchant.tax.id,
                    merchant.tax.type
                )
            } returns Optional.of(merchant)

            repository.findBy(merchant.tax.type, merchant.tax.id) shouldBe Optional.of(merchant)

            verify(exactly = 1) {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    merchant.tax.id,
                    merchant.tax.type
                )
            }
        }

        scenario("merchant NOT found") {
            val merchant = aMerchantCreated()

            every {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    merchant.tax.id,
                    merchant.tax.type
                )
            } returns Optional.empty()

            repository.findBy(merchant.tax.type, merchant.tax.id) shouldBe Optional.empty()

            verify(exactly = 1) {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    merchant.tax.id,
                    merchant.tax.type
                )
            }
        }

        feature("find by filter") {

            val pageable = PageRequest.of(0, 10)
            val status = ACTIVE
            val merchant = aMerchant()
            val merchantQuery = aMerchantQuery(status = status)

            scenario("merchant found") {

                val query = Query(Criteria.where("status").`is`(status))
                query.limit(pageable.pageSize)
                val count = 1L

                val result = PageImpl(listOf(merchant), pageable, count)

                every { mongoTemplate.count(any(), "merchants") } returns count
                every { mongoTemplate.find(any(), Merchant::class.java) } returns listOf(merchant)

                repository.findBy(merchantQuery, pageable) shouldBe result

                verify(exactly = 1) { mongoTemplate.count(any(), "merchants") }
                verify(exactly = 1) { mongoTemplate.find(any(), Merchant::class.java) }
            }

            scenario("merchants NOT found") {

                val query = Query(Criteria.where("status").`is`(status))
                query.limit(pageable.pageSize)
                val count = 0L

                val result = PageImpl(emptyList<Merchant>(), pageable, count)

                every { mongoTemplate.count(any(), "merchants") } returns count
                every { mongoTemplate.find(any(), Merchant::class.java) } returns emptyList()

                repository.findBy(merchantQuery, pageable) shouldBe result

                verify(exactly = 1) { mongoTemplate.count(any(), "merchants") }
                verify(exactly = 1) { mongoTemplate.find(any(), Merchant::class.java) }
            }
        }
    }
})
