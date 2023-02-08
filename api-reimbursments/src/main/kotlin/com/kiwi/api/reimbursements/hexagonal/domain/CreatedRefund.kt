package com.kiwi.api.reimbursements.hexagonal.domain

import java.util.UUID

data class CreatedRefund(
    val id: UUID,
    val authorization: Authorization,
    val data: Refund
)
