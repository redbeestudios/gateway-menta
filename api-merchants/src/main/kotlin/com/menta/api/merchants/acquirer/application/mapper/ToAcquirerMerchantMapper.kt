package com.menta.api.merchants.acquirer.application.mapper

import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.domain.provider.CodeProvider
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.domain.provider.IdProvider
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerMerchantMapper(
    private val idProvider: IdProvider,
    private val dateProvider: DateProvider,
    private val codeProvider: CodeProvider
) {
    fun map(preAcquirerMerchant: PreAcquirerMerchant) =
        with(preAcquirerMerchant) {
            val id = idProvider.provide()
            AcquirerMerchant(
                id = id,
                merchantId = merchantId,
                acquirer = acquirerId,
                code = codeProvider.provide(id, code),
                createDate = dateProvider.provide(),
                updateDate = dateProvider.provide()
            )
        }.log { info("created acquirer merchant mapped: {}", it) }

    companion object : CompanionLogger()
}
