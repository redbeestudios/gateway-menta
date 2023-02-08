package com.menta.api.users.shared.other.error.model.exception

import com.menta.api.users.shared.other.error.model.ApplicationError
import java.lang.RuntimeException

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
