package com.menta.api.taxesEntities.application.port.`in`

import arrow.core.Either
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.UUID

interface AddFeeRulePortIn {
    fun add(customerId: UUID, feeRule: FeeRule): Either<ApplicationError, FeeRule>
    fun select(merchantId: UUID, feeRuleIds: List<UUID>): Either<ApplicationError, List<FeeRule>>
}
