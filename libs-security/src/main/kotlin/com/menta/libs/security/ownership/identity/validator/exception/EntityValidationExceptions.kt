package com.menta.libs.security.ownership.identity.validator.exception

import com.menta.libs.security.requesterUser.model.RequesterUser.UserType

class MissingIdRequesterUserException(userType: UserType) :
    RuntimeException("$userType requester user doesnt have id")

class OwnerIdentityMismatchException(userType: UserType) :
    RuntimeException("$userType requester user doesnt match with request")

class MissingEntityIdentityValidator(userType: UserType) :
    RuntimeException("no entityIdentityValidator found for $userType")
