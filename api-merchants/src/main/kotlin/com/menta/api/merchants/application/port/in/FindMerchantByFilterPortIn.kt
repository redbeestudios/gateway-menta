package com.menta.api.merchants.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.domain.Pagination
import com.menta.api.merchants.shared.error.model.ApplicationError
import org.springframework.data.domain.Page

interface FindMerchantByFilterPortIn {
    fun execute(merchantQuery: MerchantQuery, pagination: Pagination): Either<ApplicationError, Page<Merchant>>
}
