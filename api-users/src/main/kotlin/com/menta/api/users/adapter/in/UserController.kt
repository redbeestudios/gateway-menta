package com.menta.api.users.adapter.`in`

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.menta.api.users.adapter.`in`.model.ConfirmForgotPasswordUserRequest
import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.adapter.`in`.model.ListUsersResponse
import com.menta.api.users.adapter.`in`.model.SetUserPasswordRequest
import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.adapter.`in`.model.mapper.ToListUsersResponseMapper
import com.menta.api.users.adapter.`in`.model.mapper.ToUserResponseMapper
import com.menta.api.users.application.port.`in`.ConfirmForgotPasswordUserPortIn
import com.menta.api.users.application.port.`in`.CreateUserAuthoritiesPortIn
import com.menta.api.users.application.port.`in`.CreateUserPortIn
import com.menta.api.users.application.port.`in`.DeleteUserPortIn
import com.menta.api.users.application.port.`in`.FindUserPortIn
import com.menta.api.users.application.port.`in`.ForgotPasswordUserPortIn
import com.menta.api.users.application.port.`in`.ListUsersFilterByPortIn
import com.menta.api.users.application.port.`in`.SetUserPasswordPortIn
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.mapper.ToConfirmForgotPasswordUserMapper
import com.menta.api.users.domain.mapper.ToListUsersFilterByQueryMapper
import com.menta.api.users.domain.mapper.ToNewUserMapper
import com.menta.api.users.domain.mapper.ToSetUserPasswordMapper
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.log.benchmark
import com.menta.api.users.shared.other.util.rest.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/private/users")
class UserController(
    private val findUser: FindUserPortIn,
    private val createUser: CreateUserPortIn,
    private val setUserPassword: SetUserPasswordPortIn,
    private val deleteUser: DeleteUserPortIn,
    private val forgotPassword: ForgotPasswordUserPortIn,
    private val createUserAuthoritiesPortIn: CreateUserAuthoritiesPortIn,
    private val confirmForgotPassword: ConfirmForgotPasswordUserPortIn,
    private val toNewUserMapper: ToNewUserMapper,
    private val toSetUserPasswordMapper: ToSetUserPasswordMapper,
    private val toConfirmForgotPasswordUserMapper: ToConfirmForgotPasswordUserMapper,
    private val listUsersFilterByPortIn: ListUsersFilterByPortIn,
    private val toUserResponseMapper: ToUserResponseMapper,
    private val toListUsersFilterByQueryMapper: ToListUsersFilterByQueryMapper,
    private val toListUsersResponseMapper: ToListUsersResponseMapper
) {

    @Operation(summary = "Get user by mail and type")
    @GetMapping("/{email}/type/{type}")
    @ResponseStatus(OK)
    fun get(@PathVariable email: Email, @PathVariable type: UserType): UserResponse =
        log.benchmark("find user by email: $email and type $type") {
            doFindBy(email, type)
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Create user")
    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@Valid @RequestBody request: CreateUserRequest): UserResponse =
        log.benchmark("create user") {
            request
                .toNewUser()
                .doCreate()
                .doEstablishAuthorities()
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Get user by type and filter by email, customerId and merchantId")
    @GetMapping("/type/{type}")
    @ResponseStatus(OK)
    fun get(
        @PathVariable type: UserType,
        @RequestParam(required = false) email: Email?,
        @RequestParam(required = false) customerId: UUID?,
        @RequestParam(required = false) merchantId: UUID?,
        @RequestParam(required = false) next: String?,
        @RequestParam(required = false) limit: Int?
    ): ListUsersResponse =
        log.benchmark("find user by type: $type") {
            buildQuery(type, email, customerId, merchantId, next, limit)
                .list()
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Disable user by mail and type")
    @DeleteMapping("/{email}/type/{type}")
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable email: Email, @PathVariable type: UserType): Unit =
        log.benchmark("delete user by email: $email and type $type") {
            doDeleteBy(email, type)
                .throwIfLeft()
        }

    @Operation(summary = "Set user password")
    @PostMapping("/set-password")
    @ResponseStatus(NO_CONTENT)
    fun setPassword(@Valid @RequestBody request: SetUserPasswordRequest): Unit =
        log.benchmark("set password for email: ${request.email}") {
            request.toSetUserPassword().doSetPassword()
                .throwIfLeft()
        }

    @Operation(summary = "Forgot user password")
    @PostMapping("/{email}/type/{type}/forgot-password")
    @ResponseStatus(NO_CONTENT)
    fun forgotPassword(@PathVariable email: Email, @PathVariable type: UserType): Unit =
        log.benchmark("forgot password for email $email and type $type") {
            doForgotPassword(email, type)
                .throwIfLeft()
        }

    @Operation(summary = "Confirm forgot user password")
    @PatchMapping("/{email}/type/{type}/confirm-forgot-password")
    @ResponseStatus(NO_CONTENT)
    fun confirmForgotPassword(
        @PathVariable email: Email,
        @PathVariable type: UserType,
        @Valid @RequestBody request: ConfirmForgotPasswordUserRequest
    ): Unit =
        log.benchmark("confirm forgot password for email $email and type $type") {
            request
                .toDomain(email, type)
                .doConfirmForgotPassword()
                .throwIfLeft()
        }

    private fun doFindBy(email: Email, type: UserType) =
        findUser.findBy(email, type)
            .logRight { info("user found: {}", it) }

    private fun doDeleteBy(email: Email, type: UserType) =
        deleteUser.deleteBy(email, type)
            .logRight { info("user deleted: {}", it) }

    private fun doForgotPassword(email: Email, type: UserType) =
        forgotPassword.retrieve(email, type)
            .logRight { info("forgot password: {}", it) }

    private fun ConfirmForgotPasswordUser.doConfirmForgotPassword() =
        confirmForgotPassword.confirm(this)
            .logRight { info("confirm forgot password: {}", it) }

    private fun ConfirmForgotPasswordUserRequest.toDomain(email: Email, type: UserType) =
        toConfirmForgotPasswordUserMapper.mapFrom(this, email, type)
            .log { info("confirm forgot user password mapped: {}", it) }

    private fun SetUserPassword.doSetPassword() =
        setUserPassword.setPassword(this)
            .logRight { info("password changed: {}", it) }

    private fun NewUser.doCreate() =
        createUser.create(this)
            .logRight { info("user created: {}", it) }

    private fun Either<ApplicationError, User>.doEstablishAuthorities() =
        this.flatMap { user ->
            createUserAuthoritiesPortIn.execute(user)
                .logRight { info("authorities were successfully established for user: {}", user.attributes.email) }
                .let { user.right() }
        }

    private fun CreateUserRequest.toNewUser() =
        toNewUserMapper.mapFrom(this)
            .log { info("new user mapped: {}", it) }

    private fun SetUserPasswordRequest.toSetUserPassword() =
        toSetUserPasswordMapper.mapFrom(this)
            .log { info("set user password mapped: {}", it) }

    private fun User.toResponse() =
        toUserResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    private fun buildQuery(
        type: UserType,
        email: Email?,
        customerId: UUID?,
        merchantId: UUID?,
        next: String?,
        limit: Int?
    ) =
        toListUsersFilterByQueryMapper
            .mapFrom(type, email, customerId, merchantId, limit, next)
            .log { info("query mapped from request inputs") }

    private fun ListUsersFilterByQuery.list() =
        listUsersFilterByPortIn.list(this)
            .logRight { info("users listed: {}", it) }

    private fun ListUserPage.toResponse() =
        toListUsersResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
