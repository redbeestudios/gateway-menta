package com.menta.api.merchants.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.domain.LegalType.LEGAL_ENTITY
import com.menta.api.merchants.domain.LegalType.NATURAL_PERSON
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidLegalType
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidRepresentative
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

enum class LegalType {
    NATURAL_PERSON, LEGAL_ENTITY
}

@Component
class LegalTypeValidator {
    fun validate(preMerchant: PreMerchant): Either<ApplicationError, PreMerchant> = with(preMerchant) {
        if (legalType == NATURAL_PERSON && businessOwner == null) {
            invalidBusinessOwner().left()
        } else if (legalType == LEGAL_ENTITY && representative == null) {
            invalidRepresentative().left()
        } else if (representative != null && businessOwner != null) {
            invalidLegalType().left()
        }else {
            right()
        }
    }.logEither(
        {
            error("Legal Type validation failed : {}", it)
        },
        {
            info("Legal Type validated")
        }
    )

    companion object : CompanionLogger()
}
