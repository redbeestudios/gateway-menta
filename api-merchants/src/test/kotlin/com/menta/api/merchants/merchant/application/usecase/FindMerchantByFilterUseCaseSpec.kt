package com.menta.api.merchants.merchant.application.usecase

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.aMerchantQuery
import com.menta.api.merchants.acquirer.aCustomerId
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.application.usecase.FindMerchantByFilterUseCase
import com.menta.api.merchants.domain.Pagination
import com.menta.api.merchants.domain.Status.ACTIVE
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.merchantNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class FindMerchantByFilterUseCaseSpec : FeatureSpec({
    val repository = mockk<MerchantRepositoryOutPort>()
    val useCase = FindMerchantByFilterUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find by status") {

        val merchantQuery = aMerchantQuery(status = ACTIVE, customerId = aCustomerId)
        val pagination = Pagination(0, 10)
        val pageRequest = PageRequest.of(0, 10)
        val merchant = aMerchant()
        scenario("merchant found") {

            every { repository.findBy(merchantQuery, pageRequest) } returns PageImpl(listOf(merchant))

            useCase.execute(merchantQuery, pagination) shouldBeRight PageImpl(listOf(merchant))

            verify(exactly = 1) { repository.findBy(merchantQuery, pageRequest) }
        }

        scenario("merchant NOT found") {

            every { repository.findBy(merchantQuery, pageRequest) } returns PageImpl(emptyList())

            useCase.execute(merchantQuery, pagination) shouldBeLeft merchantNotFound(merchantQuery)

            verify(exactly = 1) { repository.findBy(merchantQuery, pageRequest) }
        }
    }
})
