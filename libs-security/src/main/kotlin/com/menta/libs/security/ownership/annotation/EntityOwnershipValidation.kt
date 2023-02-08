package com.menta.libs.security.ownership.annotation

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(FUNCTION)
@MustBeDocumented
annotation class EntityOwnershipValidation(

    /**
     * Dueño de la entidad a validar
     */
    val owner: UserType,

    /**
     * Nombre con el que buscar el argumento a validar
     */
    val argumentName: String,

    /**
     * Especifica de donde debe obtenerse el argumento
     * [EntityOwnershipArgumentSource.PATH_VARIABLE] path de la uri.
     * [EntityOwnershipArgumentSource.QUERY_PARAMETER] query de la uri.
     * [EntityOwnershipArgumentSource.BODY_ATTRIBUTE] atributo en el body del request.
     */
    val argumentSource: EntityOwnershipArgumentSource,
)


@Retention(RUNTIME)
@Target(FUNCTION)
@MustBeDocumented
annotation class EntityOwnershipValidations(
    val validations: Array<EntityOwnershipValidation>
)