package com.menta.api.customers.customer.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidLegalType
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidRepresentative
import com.menta.api.customers.shared.error.model.InvalidLegalTypeError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

enum class LegalType {
    NATURAL_PERSON, LEGAL_ENTITY;

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): LegalType =
            LegalType.values().find { it.name == value } ?: throw InvalidLegalTypeError(value)
    }
}

@Component
class LegalTypeValidator {
    fun validate(preCustomer: PreCustomer): Either<ApplicationError, PreCustomer> =
        with(preCustomer) {
            if (legalType == LegalType.NATURAL_PERSON && businessOwner == null) {
                invalidBusinessOwner().left()
            } else if (legalType == LegalType.LEGAL_ENTITY && representative == null) {
                invalidRepresentative().left()
            } else if (representative != null && businessOwner != null) {
                invalidLegalType().left()
            } else {
                right()
            }
        }.logEither(
            { error("Legal Type validation failed : {}", it) },
            { info("Legal Type validated") }
        )

    companion object : CompanionLogger()
}
