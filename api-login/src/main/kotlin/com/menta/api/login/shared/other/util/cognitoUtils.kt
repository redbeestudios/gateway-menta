package com.menta.api.login.shared.other.util

import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException
import com.amazonaws.services.cognitoidp.model.PasswordResetRequiredException
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException
import com.amazonaws.services.cognitoidp.model.UserNotFoundException
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.passwordResetRequired
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.userNotConfirmed

fun Throwable.handleCognitoError() =
    when (this) {
        is InvalidParameterException, is NotAuthorizedException,
        is UserNotFoundException, is ResourceNotFoundException,
        is InvalidPasswordException -> unauthorizedUser(this)
        is PasswordResetRequiredException -> passwordResetRequired()
        is UserNotConfirmedException -> userNotConfirmed()
        else -> unhandledException(this)
    }

