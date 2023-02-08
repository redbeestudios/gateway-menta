package com.menta.libs.security.requesterUser.exception

class InvalidRequesterUserException(field: String) :
    RuntimeException("requester user has no $field")
