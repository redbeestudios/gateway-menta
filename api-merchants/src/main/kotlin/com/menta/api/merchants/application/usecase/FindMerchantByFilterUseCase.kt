package com.menta.api.merchants.application.usecase

import arrow.core.Either
import com.menta.api.merchants.acquirer.adapter.`in`.controller.AcquirerMerchantController.Companion.log
import com.menta.api.merchants.acquirer.adapter.`in`.controller.AcquirerMerchantController.Companion.logEither
import com.menta.api.merchants.application.port.`in`.FindMerchantByFilterPortIn
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.domain.Pagination
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.merchantNotFound
import com.menta.api.merchants.shared.utils.either.rightIfPresent
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FindMerchantByFilterUseCase(
    private val merchantRepository: MerchantRepositoryOutPort
) : FindMerchantByFilterPortIn {
    override fun execute(merchantQuery: MerchantQuery, pagination: Pagination): Either<ApplicationError, Page<Merchant>> {
        return findBy(merchantQuery, pageable(pagination))
            .shouldBePresent(merchantQuery)
    }

    private fun findBy(
        merchantQuery: MerchantQuery,
        pageable: Pageable
    ) =
        merchantRepository.findBy(merchantQuery, pageable)
            .log { info("merchant searched") }

    private fun Page<Merchant>.shouldBePresent(merchantQuery: MerchantQuery) =
        rightIfPresent(
            error = merchantNotFound(merchantQuery)
        )
            .logEither(
                {
                    error(
                        "merchant with " + filterPresent(merchantQuery) + " not found."
                    )
                },
                {
                    info(
                        "merchant with " + filterPresent(merchantQuery) + "found."
                    )
                }
            )

    private fun filterPresent(merchantQuery: MerchantQuery) =
        with(merchantQuery) {
            listOfNotNull(
                status?.let { "status: $status" },
                merchantId?.let { "merchantId: $merchantId" },
                customerId?.let { "customerId: $customerId" },
            ).joinToString(", ")
        }

    private fun pageable(pagination: Pagination): Pageable =
        PageRequest.of(pagination.page, pagination.size)
}
