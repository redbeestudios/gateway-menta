package com.menta.api.transactions.application.port.`in`

import com.menta.api.transactions.domain.Transaction

interface CreateOperationInPort {
    fun execute(transaction: Transaction)
}
