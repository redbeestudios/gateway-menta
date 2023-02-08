package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund

interface CreateOperationInPort {
    fun execute(createdRefund: CreatedRefund)
    fun execute(createdAnnulment: CreatedAnnulment)
}
