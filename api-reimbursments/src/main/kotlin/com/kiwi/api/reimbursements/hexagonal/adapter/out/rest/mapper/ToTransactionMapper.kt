package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.TransactionResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import org.springframework.stereotype.Component

@Component
class ToTransactionMapper {
    fun map(response: TransactionResponse): Transaction =
        with(response) {
            Transaction(
                id = id
            )
        }
}
