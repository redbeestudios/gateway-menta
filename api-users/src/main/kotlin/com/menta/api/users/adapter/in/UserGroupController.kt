package com.menta.api.users.adapter.`in`

import arrow.core.flatMap
import com.menta.api.users.adapter.`in`.model.AssignGroupRequest
import com.menta.api.users.adapter.`in`.model.AssignGroupResponse
import com.menta.api.users.adapter.`in`.model.ListGroupsByUserResponse
import com.menta.api.users.adapter.`in`.model.mapper.ToAssignGroupResponseMapper
import com.menta.api.users.adapter.`in`.model.mapper.ToListGroupsByUserResponseMapper
import com.menta.api.users.application.port.`in`.AssignGroupPortIn
import com.menta.api.users.application.port.`in`.FindUserPortIn
import com.menta.api.users.application.port.`in`.ListGroupsByUserPortIn
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.domain.mapper.ToGroupAssignationMapper
import com.menta.api.users.domain.mapper.ToListGroupByUserQueryMapper
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.rest.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/private/users/{email}/type/{type}/groups")
class UserGroupController(
    private val findUser: FindUserPortIn,
    private val assignGroup: AssignGroupPortIn,
    private val listGroupsByUser: ListGroupsByUserPortIn,
    private val toGroupAssignationMapper: ToGroupAssignationMapper,
    private val toAssignGroupResponseMapper: ToAssignGroupResponseMapper,
    private val toListGroupByUserQueryMapper: ToListGroupByUserQueryMapper,
    private val toListGroupsByUserResponseMapper: ToListGroupsByUserResponseMapper
) {

    @Operation(summary = "Assign group to user")
    @PostMapping
    @ResponseStatus(CREATED)
    fun assignGroup(
        @Valid @RequestBody request: AssignGroupRequest,
        @PathVariable email: Email,
        @PathVariable type: UserType
    ): AssignGroupResponse =
        findUserBy(email, type).flatMap { user ->
            buildGroupAssignation(request, user)
                .doAssign()
        }
            .throwIfLeft()
            .toResponse()

    @Operation(summary = "List groups assigned to user")
    @GetMapping
    @ResponseStatus(OK)
    fun listGroupsForUser(
        @PathVariable email: Email,
        @PathVariable type: UserType,
        @RequestParam(required = false) next: String?,
        @RequestParam(required = false) limit: Int?
    ): ListGroupsByUserResponse =
        findUserBy(email, type).flatMap { user ->
            buildQuery(user, limit, next)
                .list()
        }
            .throwIfLeft()
            .toResponse()

    private fun findUserBy(email: Email, type: UserType) =
        findUser.findBy(email, type)
            .logRight { info("user found: {}", it) }

    private fun buildGroupAssignation(request: AssignGroupRequest, user: User) =
        toGroupAssignationMapper.mapFrom(request, user)
            .log { info("group assignation builded: {}", it) }

    private fun GroupAssignation.doAssign() =
        assignGroup.assign(this)
            .logRight { info("group assigned: {}", it) }

    private fun GroupAssignation.toResponse() =
        toAssignGroupResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    private fun buildQuery(user: User, limit: Int?, next: String?) =
        toListGroupByUserQueryMapper.mapFrom(user, next, limit)
            .log { info("query mapped from request inputs") }

    private fun ListGroupByUserQuery.list() =
        listGroupsByUser.list(this)
            .logRight { info("group listed: {}", it) }

    private fun UserWithGroups.toResponse() =
        toListGroupsByUserResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
