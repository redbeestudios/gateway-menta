package com.kiwi.api.reimbursements.hexagonal.application.port.out

import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.Refund

interface AcquirerRepository {
    fun authorizeAnnulment(annulment: Annulment): Authorization
    fun authorizeRefund(refund: Refund): Authorization
}
