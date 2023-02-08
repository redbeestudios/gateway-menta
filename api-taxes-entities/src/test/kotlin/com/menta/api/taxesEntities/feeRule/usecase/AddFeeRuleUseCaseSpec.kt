package com.menta.api.taxesEntities.feeRule.usecase

import arrow.core.right
import com.menta.api.taxesEntities.aFeeRule
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.AddFeeRuleUseCase
import com.menta.api.taxesEntities.domain.FeeRule
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class AddFeeRuleUseCaseSpec : FeatureSpec({

    val taxCustomerRepositoryOutPort: TaxCustomerRepositoryOutPort = mockk()
    val taxMerchantRepositoryOutPort: TaxMerchantRepositoryOutPort = mockk()
    val useCase = AddFeeRuleUseCase(taxCustomerRepositoryOutPort, taxMerchantRepositoryOutPort)

    beforeEach { clearAllMocks() }

    feature("add merchant fee rule option to a tax customer") {
        scenario("add merchant fee rule option to a tax customer valid") {
            val taxCustomer = aTaxCustomer()
            val merchantFeeRules: List<FeeRule> = listOf(aTaxCustomer().feeRules!![0], aTaxCustomer().feeRules!![0])
            val taxCustomerToUpdate = taxCustomer.copy(merchantFeeRulesOptions = merchantFeeRules)

            every { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) } returns Optional.of(taxCustomer)
            every { taxCustomerRepositoryOutPort.update(taxCustomerToUpdate) } returns taxCustomerToUpdate.right()

            taxCustomer.merchantFeeRulesOptions?.get(0)?.let { useCase.add(taxCustomer.customerId, it) }
                ?.shouldBeRight(taxCustomer.merchantFeeRulesOptions!![0])

            verify { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) }
            verify { taxCustomerRepositoryOutPort.update(taxCustomerToUpdate) }
        }
    }

    feature("select merchant fee rule to a tax merchant") {
        scenario("select merchant fee rule to a tax merchant valid") {
            val taxCustomer = aTaxCustomer()
            val taxMerchant = aTaxMerchant().copy(feeRules = listOf(aFeeRule()))
            val ids = listOf(UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"))
            val merchantFeeRules: List<FeeRule> = listOf(taxMerchant.feeRules!![0])
            val taxMerchantToUpdate = taxMerchant.copy(feeRules = merchantFeeRules)

            every { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) } returns Optional.of(taxCustomer)
            every { taxMerchantRepositoryOutPort.findBy(taxMerchant.merchantId) } returns Optional.of(taxMerchant)
            every { taxMerchantRepositoryOutPort.update(any()) } returns taxMerchantToUpdate.right()
            useCase.select(taxMerchant.merchantId, ids)

            verify { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) }
            verify { taxMerchantRepositoryOutPort.findBy(taxMerchant.merchantId) }
            verify { taxMerchantRepositoryOutPort.update(any()) }
        }

        scenario("when select merchant fee rule to an existing tax merchant") {
            val taxCustomer = aTaxCustomer()
            val taxMerchant = aTaxMerchant()
            val ids = listOf(UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"))
            val merchantFeeRules: List<FeeRule> = listOf(taxMerchant.feeRules!![0])
            val taxMerchantToUpdate = taxMerchant.copy(feeRules = merchantFeeRules)

            every { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) } returns Optional.of(taxCustomer)
            every { taxMerchantRepositoryOutPort.findBy(taxMerchant.merchantId) } returns Optional.of(taxMerchant)
            every { taxMerchantRepositoryOutPort.update(any()) } returns taxMerchantToUpdate.right()

            useCase.select(taxMerchant.merchantId, ids)

            verify { taxCustomerRepositoryOutPort.findBy(taxCustomer.customerId) }
            verify { taxMerchantRepositoryOutPort.findBy(taxMerchant.merchantId) }
            verify { taxMerchantRepositoryOutPort.update(any()) }
        }
    }
})
