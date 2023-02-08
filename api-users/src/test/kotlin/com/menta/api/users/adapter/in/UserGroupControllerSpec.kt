package com.menta.api.users.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.users.aGroupAssignation
import com.menta.api.users.aListGroupByUserQuery
import com.menta.api.users.aUser
import com.menta.api.users.aUserWithGroups
import com.menta.api.users.adapter.`in`.model.AssignGroupRequest
import com.menta.api.users.adapter.`in`.model.AssignGroupResponse
import com.menta.api.users.adapter.`in`.model.ListGroupsByUserResponse
import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.adapter.`in`.model.mapper.ToAssignGroupResponseMapper
import com.menta.api.users.adapter.`in`.model.mapper.ToListGroupsByUserResponseMapper
import com.menta.api.users.application.port.`in`.AssignGroupPortIn
import com.menta.api.users.application.port.`in`.FindUserPortIn
import com.menta.api.users.application.port.`in`.ListGroupsByUserPortIn
import com.menta.api.users.createDate
import com.menta.api.users.customerId
import com.menta.api.users.domain.UserStatus.CONFIRMED
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.mapper.ToGroupAssignationMapper
import com.menta.api.users.domain.mapper.ToListGroupByUserQueryMapper
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.resourceNotFound
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userNotFound
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

class UserGroupControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val findUser = mockk<FindUserPortIn>()
    val assignGroup = mockk<AssignGroupPortIn>()
    val listGroupsByUser = mockk<ListGroupsByUserPortIn>()
    val toGroupAssignationMapper = mockk<ToGroupAssignationMapper>()
    val toListGroupByUserQueryMapper = mockk<ToListGroupByUserQueryMapper>()
    val toAssignGroupResponseMapper = mockk<ToAssignGroupResponseMapper>()
    val toListGroupsByUserResponseMapper = mockk<ToListGroupsByUserResponseMapper>()

    val controller = UserGroupController(
        findUser = findUser,
        assignGroup = assignGroup,
        listGroupsByUser = listGroupsByUser,
        toGroupAssignationMapper = toGroupAssignationMapper,
        toAssignGroupResponseMapper = toAssignGroupResponseMapper,
        toListGroupByUserQueryMapper = toListGroupByUserQueryMapper,
        toListGroupsByUserResponseMapper = toListGroupsByUserResponseMapper
    )

    val objectMapper = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .build<ObjectMapper>()

    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(aControllerAdvice(httpServletRequest))
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.queryString } returns null
    }

    feature("get list group") {
        scenario("successful get group of user") {
            val user = aUser()
            val query = aListGroupByUserQuery()
            val userWithGroups = aUserWithGroups()
            val response = ListGroupsByUserResponse(
                user = UserResponse(
                    attributes = UserResponse.Attributes(
                        email = email,
                        merchantId = merchantId.toString(),
                        customerId = customerId.toString(),
                        type = MERCHANT
                    ),
                    status = CONFIRMED,
                    audit = UserResponse.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                ),
                groups = listOf(
                    ListGroupsByUserResponse.Group(
                        name = "name",
                        description = "description",
                        audit = ListGroupsByUserResponse.Group.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
                ),
                _metadata = ListGroupsByUserResponse.SearchMetadata(
                    _next = null,
                    _limit = null
                )
            )

            every { httpServletRequest.requestURI } returns "/private/users/${query.user.attributes.email}/type/${query.user.attributes.type}/groups"
            every { findUser.findBy(query.user.attributes.email, query.user.attributes.type) } returns user.right()
            every { toListGroupByUserQueryMapper.mapFrom(user = user, next = null, limit = null) } returns query
            every { listGroupsByUser.list(query) } returns userWithGroups.right()
            every { toListGroupsByUserResponseMapper.mapFrom(userWithGroups) } returns response

            mockMvc
                .perform(
                    get("/private/users/${query.user.attributes.email}/type/${query.user.attributes.type}/groups")
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.user.status").value(response.user.status.toString()))
                .andExpect(jsonPath("$.user.audit.creation_date").exists())
                .andExpect(jsonPath("$.user.audit.update_date").exists())
                .andExpect(jsonPath("$.user.attributes.email").value(response.user.attributes.email))
                .andExpect(jsonPath("$.user.attributes.merchant_id").value(response.user.attributes.merchantId))
                .andExpect(jsonPath("$.user.attributes.customer_id").value(response.user.attributes.customerId))
                .andExpect(jsonPath("$.user.attributes.type").value(response.user.attributes.type.toString()))
                .andExpect(jsonPath("$.groups[0].name").value(response.groups[0].name))
                .andExpect(jsonPath("$.groups[0].description").value(response.groups[0].description))

            verify(exactly = 1) { findUser.findBy(query.user.attributes.email, query.user.attributes.type) }
            verify(exactly = 1) { toListGroupByUserQueryMapper.mapFrom(user = user, next = null, limit = null) }
            verify(exactly = 1) { listGroupsByUser.list(query) }
            verify(exactly = 1) { toListGroupsByUserResponseMapper.mapFrom(userWithGroups) }
        }
        scenario("unsuccessful get group of user NOT FOUND") {
            val user = aUser()
            val query = aListGroupByUserQuery()
            val error = userNotFound(email)

            every { httpServletRequest.requestURI } returns "/private/users/${query.user.attributes.email}/type/${query.user.attributes.type}/groups"
            every { findUser.findBy(query.user.attributes.email, query.user.attributes.type) } returns error.left()

            mockMvc
                .perform(
                    get("/private/users/${query.user.attributes.email}/type/${query.user.attributes.type}/groups")
                )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users/${query.user.attributes.email}/type/${query.user.attributes.type}/groups"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("user $email not found"))

            verify(exactly = 1) { findUser.findBy(query.user.attributes.email, query.user.attributes.type) }
            verify(exactly = 0) { toListGroupByUserQueryMapper.mapFrom(any(), any(), any()) }
            verify(exactly = 0) { listGroupsByUser.list(any()) }
            verify(exactly = 0) { toListGroupsByUserResponseMapper.mapFrom(any()) }
        }
    }
    feature("assign group") {
        scenario("assign group to user") {
            val userMail = email
            val type = MERCHANT
            val user = aUser()
            val group = aGroupAssignation()

            val request = AssignGroupRequest(name = "Payment::Create")
            val response = AssignGroupResponse(
                user = UserResponse(
                    attributes = UserResponse.Attributes(
                        email = email,
                        merchantId = merchantId.toString(),
                        customerId = customerId.toString(),
                        type = MERCHANT
                    ),
                    status = CONFIRMED,
                    audit = UserResponse.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                ),
                group = AssignGroupResponse.Group(
                    name = "Payment::Create"
                )
            )

            val json = """
                {
                    "name": "Payment::Create"
                }
            """.trimIndent()

            every { httpServletRequest.requestURI } returns "/private/users/$userMail/type/$type/groups"
            every { findUser.findBy(userMail, type) } returns user.right()
            every { toGroupAssignationMapper.mapFrom(request, user) } returns group
            every { assignGroup.assign(group) } returns group.right()
            every { toAssignGroupResponseMapper.mapFrom(group) } returns response

            mockMvc
                .perform(
                    post("/private/users/$userMail/type/$type/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.user.status").value(response.user.status.toString()))
                .andExpect(jsonPath("$.user.audit.creation_date").exists())
                .andExpect(jsonPath("$.user.audit.update_date").exists())
                .andExpect(jsonPath("$.user.attributes.email").value(response.user.attributes.email))
                .andExpect(jsonPath("$.user.attributes.merchant_id").value(response.user.attributes.merchantId))
                .andExpect(jsonPath("$.user.attributes.customer_id").value(response.user.attributes.customerId))
                .andExpect(jsonPath("$.user.attributes.type").value(response.user.attributes.type.toString()))
                .andExpect(jsonPath("$.group.name").value(response.group.name))

            verify(exactly = 1) { findUser.findBy(userMail, type) }
            verify(exactly = 1) { toGroupAssignationMapper.mapFrom(request, user) }
            verify(exactly = 1) { assignGroup.assign(group) }
            verify(exactly = 1) { toAssignGroupResponseMapper.mapFrom(group) }
        }
        scenario("user not found") {
            val userMail = email
            val type = MERCHANT
            val user = aUser()
            val group = aGroupAssignation()

            val error = userNotFound(email)

            val request = AssignGroupRequest(name = "Payment::Create")
            val json = """
                {
                    "name": "Payment::Create"
                }
            """.trimIndent()

            every { httpServletRequest.requestURI } returns "/private/users/$userMail/type/$type/groups"
            every { findUser.findBy(userMail, type) } returns error.left()

            mockMvc
                .perform(
                    post("/private/users/$userMail/type/$type/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users/$userMail/type/$type/groups"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("user $email not found"))

            verify(exactly = 1) { findUser.findBy(userMail, type) }
            verify(exactly = 0) { toGroupAssignationMapper.mapFrom(request, user) }
            verify(exactly = 0) { assignGroup.assign(group) }
            verify(exactly = 0) { toAssignGroupResponseMapper.mapFrom(group) }
        }
        scenario("resource not found") {
            val userMail = email
            val type = MERCHANT
            val user = aUser()
            val group = aGroupAssignation()
            val error = resourceNotFound(ResourceNotFoundException("group doesnt exists"))

            val request = AssignGroupRequest(name = "Payment::Creates")

            val json = """
                {
                    "name": "Payment::Creates"
                }
            """.trimIndent()

            every { httpServletRequest.requestURI } returns "/private/users/$userMail/type/$type/groups"
            every { findUser.findBy(userMail, type) } returns user.right()
            every { toGroupAssignationMapper.mapFrom(request, user) } returns group
            every { assignGroup.assign(group) } returns error.left()

            mockMvc
                .perform(
                    post("/private/users/$userMail/type/$type/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users/$userMail/type/$type/groups"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("group doesnt exists"))

            verify(exactly = 1) { findUser.findBy(userMail, type) }
            verify(exactly = 1) { toGroupAssignationMapper.mapFrom(request, user) }
            verify(exactly = 1) { assignGroup.assign(group) }
            verify(exactly = 0) { toAssignGroupResponseMapper.mapFrom(group) }
        }
    }
})
