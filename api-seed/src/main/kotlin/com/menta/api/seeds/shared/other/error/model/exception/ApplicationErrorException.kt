package com.menta.api.seeds.shared.other.error.model.exception

import com.menta.api.seeds.shared.other.error.model.ApplicationError

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
