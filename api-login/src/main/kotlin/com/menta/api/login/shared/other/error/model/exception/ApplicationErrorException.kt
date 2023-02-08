package com.menta.api.login.shared.other.error.model.exception

import com.menta.api.login.shared.other.error.model.ApplicationError
import java.lang.RuntimeException

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
