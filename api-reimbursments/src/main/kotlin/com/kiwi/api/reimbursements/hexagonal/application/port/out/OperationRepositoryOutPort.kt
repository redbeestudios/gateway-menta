package com.kiwi.api.reimbursements.hexagonal.application.port.out

import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund

interface OperationRepositoryOutPort {
    fun create(createdRefund: CreatedRefund)
    fun create(createdAnnulment: CreatedAnnulment)
}
