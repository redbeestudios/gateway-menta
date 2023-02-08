package com.kiwi.api.reimbursements.hexagonal.domain

import java.util.UUID

data class CreatedAnnulment(
    val id: UUID,
    val authorization: Authorization,
    val data: Annulment
)
