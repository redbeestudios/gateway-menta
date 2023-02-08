package com.menta.api.users.authorities.shared.other.error.model.exception

import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
