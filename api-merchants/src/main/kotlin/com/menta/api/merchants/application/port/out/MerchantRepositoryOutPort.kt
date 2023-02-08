package com.menta.api.merchants.application.port.out

import arrow.core.Either
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface MerchantRepositoryOutPort {
    fun findBy(merchantId: UUID): Optional<Merchant>
    fun findBy(taxType: String, taxId: String): Optional<Merchant>
    fun findBy(merchantQuery: MerchantQuery, pageable: Pageable): Page<Merchant>
    fun create(merchant: Merchant): Merchant
    fun update(merchant: Merchant): Either<ApplicationError, Merchant>
}
