package com.menta.api.credibanco.application.port.out

import com.menta.api.credibanco.domain.CreatedOperation

interface OperationRepositoryPortOut {
    fun save(createdOperation: CreatedOperation)
}
