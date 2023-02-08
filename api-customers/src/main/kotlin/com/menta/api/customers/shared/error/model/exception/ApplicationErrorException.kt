package com.menta.api.customers.shared.error.model.exception

import com.menta.api.customers.shared.error.model.ApplicationError
import java.lang.RuntimeException

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
