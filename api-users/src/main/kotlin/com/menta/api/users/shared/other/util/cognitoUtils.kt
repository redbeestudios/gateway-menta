package com.menta.api.users.shared.other.util

import com.amazonaws.services.cognitoidp.model.CodeMismatchException
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.amazonaws.services.cognitoidp.model.UserNotFoundException
import com.amazonaws.services.cognitoidp.model.UsernameExistsException
import com.menta.api.users.domain.Email
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.resourceNotFound
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userAlreadyExistsError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userNotFound

fun Throwable.handleCognitoError(email: Email) =
    when (this) {
        is UserNotFoundException -> userNotFound(email, this)
        is ResourceNotFoundException -> resourceNotFound(this)
        is InvalidParameterException, is NotAuthorizedException, is CodeMismatchException,
        is InvalidPasswordException -> unauthorizedUser(this)
        is UsernameExistsException -> userAlreadyExistsError(email, this)
        else -> unhandledException(this)
    }
