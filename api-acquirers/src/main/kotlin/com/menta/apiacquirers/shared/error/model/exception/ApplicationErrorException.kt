package com.menta.apiacquirers.shared.error.model.exception

import com.menta.apiacquirers.shared.error.model.ApplicationError

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
