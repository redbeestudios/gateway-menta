package com.kiwi.api.payments.domain.field

import arrow.core.Either
import com.kiwi.api.payments.adapter.jpos.provider.leftIfNull
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.InvalidAccountType
import com.kiwi.api.payments.shared.error.model.InvalidTransactionType

data class ProcessCode(
    val transactionType: TransactionType,
    val accountType: AccountType,
    val followMessage: Boolean
) {
    enum class TransactionType(val code: String) {
        PURCHASE("00"),
        PURCHASE_ANNULMENT("02"),
        REFUND("20"),
        REVERSE(""); // TODO: Ver que codigo iria para reversas

        companion object {
            private val codes: Map<String, TransactionType> = values().associateBy { it.code }

            fun from(code: String): Either<ApplicationError, TransactionType> = codes[code].leftIfNull(InvalidTransactionType(code))
        }
    }

    enum class AccountType(val code: String) {
        DEFAULT("00"),
        SAVINGS_MAESTRO("10"),
        CHECKING_MAESTRO("20");

        companion object {
            private val codes: Map<String, AccountType> = values().associateBy { it.code }

            fun from(code: String): Either<ApplicationError, AccountType> = codes[code].leftIfNull(InvalidAccountType(code))
        }
    }
}
