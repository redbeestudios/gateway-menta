package com.menta.api.users.adapter.`in`

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.menta.api.users.aConfirmForgotPasswordUser
import com.menta.api.users.aListUserPage
import com.menta.api.users.aListUsersFilterByQuery
import com.menta.api.users.aNewUser
import com.menta.api.users.aSetUserPassword
import com.menta.api.users.aUser
import com.menta.api.users.adapter.`in`.model.ConfirmForgotPasswordUserRequest
import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.adapter.`in`.model.ListUsersResponse
import com.menta.api.users.adapter.`in`.model.SetUserPasswordRequest
import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.adapter.`in`.model.mapper.ToListUsersResponseMapper
import com.menta.api.users.adapter.`in`.model.mapper.ToUserResponseMapper
import com.menta.api.users.application.port.`in`.ConfirmForgotPasswordUserPortIn
import com.menta.api.users.application.port.`in`.CreateUserAuthoritiesPortIn
import com.menta.api.users.application.port.`in`.DeleteUserPortIn
import com.menta.api.users.application.port.`in`.FindUserPortIn
import com.menta.api.users.application.port.`in`.ListUsersFilterByPortIn
import com.menta.api.users.application.usecase.CreateUserUseCase
import com.menta.api.users.application.usecase.ForgotPasswordUserUseCase
import com.menta.api.users.application.usecase.SetUserPasswordUseCase
import com.menta.api.users.createDate
import com.menta.api.users.customerId
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.mapper.ToConfirmForgotPasswordUserMapper
import com.menta.api.users.domain.mapper.ToListUsersFilterByQueryMapper
import com.menta.api.users.domain.mapper.ToNewUserMapper
import com.menta.api.users.domain.mapper.ToSetUserPasswordMapper
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.password
import com.menta.api.users.shared.other.error.ErrorHandler
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userAlreadyExistsError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userNotFound
import com.menta.api.users.shared.other.error.providers.CurrentResourceProvider
import com.menta.api.users.shared.other.error.providers.ErrorResponseMetadataProvider
import com.menta.api.users.shared.other.error.providers.ErrorResponseProvider
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import javax.servlet.http.HttpServletRequest

class UserControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val findUserUseCase = mockk<FindUserPortIn>()
    val listUsersFilterByPortIn = mockk<ListUsersFilterByPortIn>()
    val deleteUserUseCase = mockk<DeleteUserPortIn>()
    val createUserUseCase = mockk<CreateUserUseCase>()
    val createdUserAuthorities = mockk<CreateUserAuthoritiesPortIn>()
    val forgotPasswordUserUseCase = mockk<ForgotPasswordUserUseCase>()
    val confirmForgotPasswordUserUseCase = mockk<ConfirmForgotPasswordUserPortIn>()
    val setUserPasswordUseCase = mockk<SetUserPasswordUseCase>()
    val toUserResponseMapper = mockk<ToUserResponseMapper>()
    val toListUsersFilterByQueryMapper = mockk<ToListUsersFilterByQueryMapper>()
    val toListUsersResponseMapper = mockk<ToListUsersResponseMapper>()
    val toConfirmForgotPasswordUserMapper = mockk<ToConfirmForgotPasswordUserMapper>()
    val toNewUserMapper = mockk<ToNewUserMapper>()
    val toSetUserPasswordMapper = mockk<ToSetUserPasswordMapper>()
    val currentResourceProvider: CurrentResourceProvider = mockk()
    val metadataProvider: ErrorResponseMetadataProvider = mockk()

    val controller = UserController(
        findUser = findUserUseCase,
        createUser = createUserUseCase,
        createUserAuthoritiesPortIn= createdUserAuthorities,
        deleteUser = deleteUserUseCase,
        toNewUserMapper = toNewUserMapper,
        listUsersFilterByPortIn = listUsersFilterByPortIn,
        forgotPassword = forgotPasswordUserUseCase,
        confirmForgotPassword = confirmForgotPasswordUserUseCase,
        toUserResponseMapper = toUserResponseMapper,
        toListUsersFilterByQueryMapper = toListUsersFilterByQueryMapper,
        toListUsersResponseMapper = toListUsersResponseMapper,
        setUserPassword = setUserPasswordUseCase,
        toSetUserPasswordMapper = toSetUserPasswordMapper,
        toConfirmForgotPasswordUserMapper = toConfirmForgotPasswordUserMapper
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

    feature("get user by email and type") {
        val email = "user@user.com"
        val userType = MERCHANT
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))
        val user = aUser()

        scenario("successful get user") {
            val userResponse = UserResponse(
                attributes = UserResponse.Attributes(
                    email = "user@user.com",
                    merchantId = "a merchantId",
                    customerId = "a customerId",
                    type = MERCHANT
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { httpServletRequest.requestURI } returns "/private/users/$email/type/$userType"
            every { findUserUseCase.findBy(email, userType) } returns user.right()
            every { toUserResponseMapper.mapFrom(user) } returns userResponse

            mockMvc
                .perform(get("/private/users/$email/type/$userType"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value(userResponse.status.toString()))
                .andExpect(jsonPath("$.audit.creation_date").exists())
                .andExpect(jsonPath("$.audit.update_date").exists())
                .andExpect(jsonPath("$.attributes.email").value(userResponse.attributes.email))
                .andExpect(jsonPath("$.attributes.merchant_id").value(userResponse.attributes.merchantId))
                .andExpect(jsonPath("$.attributes.customer_id").value(userResponse.attributes.customerId))

            verify(exactly = 1) { findUserUseCase.findBy(email, userType) }
            verify(exactly = 1) { toUserResponseMapper.mapFrom(user) }
        }
        scenario("successful get user without merchantId") {
            val userResponse = UserResponse(
                attributes = UserResponse.Attributes(
                    email = "user@user.com",
                    merchantId = null,
                    customerId = "a customerId",
                    type = CUSTOMER
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { httpServletRequest.requestURI } returns "/private/users/$email/type/$userType"
            every { findUserUseCase.findBy(email, userType) } returns user.right()
            every { toUserResponseMapper.mapFrom(user) } returns userResponse

            mockMvc
                .perform(get("/private/users/$email/type/$userType"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value(userResponse.status.toString()))
                .andExpect(jsonPath("$.audit.creation_date").exists())
                .andExpect(jsonPath("$.audit.update_date").exists())
                .andExpect(jsonPath("$.attributes.email").value(userResponse.attributes.email))
                .andExpect(jsonPath("$.attributes.merchant_id").doesNotExist())
                .andExpect(jsonPath("$.attributes.customer_id").value(userResponse.attributes.customerId))

            verify(exactly = 1) { findUserUseCase.findBy(email, userType) }
            verify(exactly = 1) { toUserResponseMapper.mapFrom(user) }
        }
        scenario("unsuccessful get user NOT FOUND") {
            val error = userNotFound(email)

            every { httpServletRequest.requestURI } returns "/private/users/$email/type/$userType"
            every { findUserUseCase.findBy(email, userType) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/users/$email/type/$userType"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(get("/private/users/$email/type/$userType"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users/$email/type/$userType"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { findUserUseCase.findBy(email, userType) }
        }
    }

    feature("delete user by email and type") {
        val email = "user@user.com"
        val userType = MERCHANT

        scenario("successful delete user") {

            every { httpServletRequest.requestURI } returns "/private/users/$email/type/$userType"
            every { deleteUserUseCase.deleteBy(email, userType) } returns Unit.right()

            mockMvc
                .perform(delete("/private/users/$email/type/$userType"))
                .andExpect(status().isNoContent)

            verify(exactly = 1) { deleteUserUseCase.deleteBy(email, userType) }
        }

        scenario("unsuccessful delete user NOT FOUND") {
            val error = userNotFound(email)

            every { httpServletRequest.requestURI } returns "/private/users/$email/type/$userType"
            every { deleteUserUseCase.deleteBy(email, userType) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/users/$email/type/$userType"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(delete("/private/users/$email/type/$userType"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users/$email/type/$userType"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { deleteUserUseCase.deleteBy(email, userType) }
        }
    }

    feature("forgot password user by email and type") {
        val email = "user@user.com"
        val userType = MERCHANT
        val url = "/private/users/$email/type/$userType/forgot-password"

        scenario("successful forgot password user") {
            every { httpServletRequest.requestURI } returns url
            every { forgotPasswordUserUseCase.retrieve(email, userType) } returns Unit.right()

            mockMvc
                .perform(post(url))
                .andExpect(status().isNoContent)

            verify(exactly = 1) { forgotPasswordUserUseCase.retrieve(email, userType) }
        }
        scenario("unsuccessful forgot password user error communicating with cognito") {
            val error = unauthorizedUser()

            every { httpServletRequest.requestURI } returns url
            every { forgotPasswordUserUseCase.retrieve(email, userType) } returns error.left()
            every { currentResourceProvider.provideUri() } returns url
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(post(url))
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(url))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) { forgotPasswordUserUseCase.retrieve(email, userType) }
        }
    }

    feature("confirm forgot password user by email and type") {
        val email = "user@user.com"
        val userType = MERCHANT
        val url = "/private/users/$email/type/$userType/confirm-forgot-password"
        val confirmPasswordRequest = ConfirmForgotPasswordUserRequest(
            password = "$password",
            code = "code"
        )
        val confirmPasswordUser = aConfirmForgotPasswordUser()

        scenario("successful confirm forgot password user") {
            every { httpServletRequest.requestURI } returns url
            every {
                toConfirmForgotPasswordUserMapper.mapFrom(
                    confirmPasswordRequest, email, userType
                )
            } returns confirmPasswordUser
            every { confirmForgotPasswordUserUseCase.confirm(confirmPasswordUser) } returns Unit.right()

            mockMvc
                .perform(
                    patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestConfirmPasswordUser())
                )
                .andExpect(status().isNoContent)

            verify(exactly = 1) {
                toConfirmForgotPasswordUserMapper.mapFrom(
                    confirmPasswordRequest, email, userType
                )
            }
            verify(exactly = 1) { confirmForgotPasswordUserUseCase.confirm(confirmPasswordUser) }
        }
        scenario("unsuccessful confirm forgot password user error communicating with cognito") {
            val error = unauthorizedUser()

            every { httpServletRequest.requestURI } returns url
            every {
                toConfirmForgotPasswordUserMapper.mapFrom(
                    confirmPasswordRequest, email, userType
                )
            } returns confirmPasswordUser
            every { confirmForgotPasswordUserUseCase.confirm(confirmPasswordUser) } returns error.left()
            every { currentResourceProvider.provideUri() } returns url
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(
                    patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestConfirmPasswordUser())
                )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value(url))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").exists())

            verify(exactly = 1) {
                toConfirmForgotPasswordUserMapper.mapFrom(
                    confirmPasswordRequest, email, userType
                )
            }
            verify(exactly = 1) { confirmForgotPasswordUserUseCase.confirm(confirmPasswordUser) }
        }
    }

    feature("create user") {
        val newUser = aNewUser()
        val user = aUser()
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("successful create new user") {
            val createUserRequest = CreateUserRequest(
                userType = MERCHANT,
                attributes = CreateUserRequest.Attributes(
                    email = email,
                    merchantId = merchantId,
                    customerId = customerId
                )
            )

            val userResponse = UserResponse(
                attributes = UserResponse.Attributes(
                    email = email,
                    merchantId = merchantId.toString(),
                    customerId = customerId.toString(),
                    type = MERCHANT
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { httpServletRequest.requestURI } returns "/private/users"
            every { toNewUserMapper.mapFrom(createUserRequest) } returns newUser
            every { createUserUseCase.create(newUser) } returns user.right()
            every { createdUserAuthorities.execute(user)  } returns Unit.right()
            every { toUserResponseMapper.mapFrom(user) } returns userResponse

            mockMvc
                .perform(
                    post("/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestCreateNewUser())
                )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.status").value(userResponse.status.toString()))
                .andExpect(jsonPath("$.audit.creation_date").exists())
                .andExpect(jsonPath("$.audit.update_date").exists())
                .andExpect(jsonPath("$.attributes.email").value(userResponse.attributes.email))
                .andExpect(jsonPath("$.attributes.merchant_id").value(userResponse.attributes.merchantId))
                .andExpect(jsonPath("$.attributes.customer_id").value(userResponse.attributes.customerId))

            verify(exactly = 1) { toNewUserMapper.mapFrom(createUserRequest) }
            verify(exactly = 1) { createUserUseCase.create(newUser) }
            verify(exactly = 1) { toUserResponseMapper.mapFrom(user) }
            verify(exactly = 1) { createdUserAuthorities.execute(user)  }
        }
        scenario("unsuccessful create new user with user exist UNPROCESSABLE ENTITY") {
            val createUserRequest = CreateUserRequest(
                userType = MERCHANT,
                attributes = CreateUserRequest.Attributes(
                    email = email,
                    merchantId = merchantId,
                    customerId = customerId
                )
            )
            val error = userAlreadyExistsError(email)

            every { httpServletRequest.requestURI } returns "/private/users"
            every { toNewUserMapper.mapFrom(createUserRequest) } returns newUser
            every { createUserUseCase.create(newUser) } returns error.left()
            every { currentResourceProvider.provideUri() } returns "/private/users"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")

            mockMvc
                .perform(
                    post("/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequestCreateNewUser())
                )
                .andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(jsonPath("errors[0].resource").value("/private/users"))
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("user $email already exists"))

            verify(exactly = 1) { toNewUserMapper.mapFrom(createUserRequest) }
            verify(exactly = 1) { createUserUseCase.create(newUser) }
            verify(exactly = 0) { toUserResponseMapper.mapFrom(any()) }
        }
    }
    feature("get users filter by") {
        val query = aListUsersFilterByQuery()
        val result = aListUserPage()
        scenario("successful filter by type") {
            val userResponse = UserResponse(
                attributes = UserResponse.Attributes(
                    type = MERCHANT,
                    email = email,
                    customerId = "a customerId",
                    merchantId = "a merchantId"
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )
            val listUserResponse = ListUsersResponse(
                users = listOf(userResponse),
                _metadata = ListUsersResponse.SearchMetadata(
                    _next = null,
                    _limit = null
                )
            )

            every { httpServletRequest.requestURI } returns "/private/users/type/${query.type}"
            every {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = null,
                    customerId = null,
                    merchantId = null,
                    limit = null,
                    next = null
                )
            } returns query
            every { listUsersFilterByPortIn.list(query) } returns result.right()
            every { toListUsersResponseMapper.mapFrom(result) } returns listUserResponse

            mockMvc
                .perform(get("/private/users/type/${query.type}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.users[0].status").value(userResponse.status.toString()))
                .andExpect(jsonPath("$.users[0].audit.creation_date").exists())
                .andExpect(jsonPath("$.users[0].audit.update_date").exists())
                .andExpect(jsonPath("$.users[0].attributes.type").value(userResponse.attributes.type.toString()))
                .andExpect(jsonPath("$.users[0].attributes.email").value(userResponse.attributes.email))
                .andExpect(jsonPath("$.users[0].attributes.merchant_id").value(userResponse.attributes.merchantId))
                .andExpect(jsonPath("$.users[0].attributes.customer_id").value(userResponse.attributes.customerId))
                .andExpect(jsonPath("$.metadata.next").doesNotExist())
                .andExpect(jsonPath("$.metadata.limit").doesNotExist())

            verify(exactly = 1) {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = null,
                    customerId = null,
                    merchantId = null,
                    limit = null,
                    next = null
                )
            }
            verify(exactly = 1) { listUsersFilterByPortIn.list(query) }
            verify(exactly = 1) { toListUsersResponseMapper.mapFrom(result) }
        }
        scenario("successful with all filters") {
            val query = query.copy(
                email = email,
                customerId = customerId,
                merchantId = merchantId,
                search = ListUsersFilterByQuery.Search(
                    limit = 10,
                    next = "token"
                )
            )
            val userResponse = UserResponse(
                attributes = UserResponse.Attributes(
                    type = MERCHANT,
                    email = email,
                    customerId = customerId.toString(),
                    merchantId = merchantId.toString()
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )
            val listUserResponse = ListUsersResponse(
                users = listOf(userResponse),
                _metadata = ListUsersResponse.SearchMetadata(
                    _next = "token",
                    _limit = 10
                )
            )

            every { httpServletRequest.requestURI } returns
                "/private/users/type/${query.type}" +
                "?email=${query.email}" +
                "&customerId=${query.customerId}" +
                "&merchantId=${query.merchantId}" +
                "&limit=${query.search.limit}" +
                "&next=${query.search.next}"
            every {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = email,
                    customerId = customerId,
                    merchantId = merchantId,
                    limit = 10,
                    next = "token"
                )
            } returns query
            every { listUsersFilterByPortIn.list(query) } returns result.right()
            every { toListUsersResponseMapper.mapFrom(result) } returns listUserResponse

            val requestParams: MultiValueMap<String, String> = LinkedMultiValueMap()
            requestParams.add("email", query.email)
            requestParams.add("customerId", query.customerId.toString())
            requestParams.add("merchantId", query.merchantId.toString())
            requestParams.add("limit", query.search.limit.toString())
            requestParams.add("next", query.search.next)

            mockMvc
                .perform(get("/private/users/type/${query.type}").params(requestParams))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.users[0].status").value(userResponse.status.toString()))
                .andExpect(jsonPath("$.users[0].audit.creation_date").exists())
                .andExpect(jsonPath("$.users[0].audit.update_date").exists())
                .andExpect(jsonPath("$.users[0].attributes.type").value(userResponse.attributes.type.toString()))
                .andExpect(jsonPath("$.users[0].attributes.email").value(userResponse.attributes.email))
                .andExpect(jsonPath("$.users[0].attributes.merchant_id").value(userResponse.attributes.merchantId))
                .andExpect(jsonPath("$.users[0].attributes.customer_id").value(userResponse.attributes.customerId))
                .andExpect(jsonPath("$.metadata.next").value(listUserResponse._metadata._next))
                .andExpect(jsonPath("$.metadata.limit").value(listUserResponse._metadata._limit))

            verify(exactly = 1) {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = email,
                    customerId = customerId,
                    merchantId = merchantId,
                    limit = 10,
                    next = "token"
                )
            }
            verify(exactly = 1) { listUsersFilterByPortIn.list(query) }
            verify(exactly = 1) { toListUsersResponseMapper.mapFrom(result) }
        }
        scenario("unsuccessful find user by email NOT FOUND") {
            val query = query.copy(email = email)
            val error = userNotFound(email)

            every { currentResourceProvider.provideUri() } returns "/private/users/type/${query.type}?email=${query.email}"
            every { metadataProvider.provide() } returns mapOf("query_string" to "")
            every { httpServletRequest.requestURI } returns "/private/users/type/${query.type}?email=${query.email}"
            every {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = email,
                    customerId = null,
                    merchantId = null,
                    limit = null,
                    next = null
                )
            } returns query
            every { listUsersFilterByPortIn.list(query) } returns error.left()

            val requestParams: MultiValueMap<String, String> = LinkedMultiValueMap()
            requestParams.add("email", query.email)

            mockMvc
                .perform(get("/private/users/type/${query.type}").params(requestParams))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("datetime").exists())
                .andExpect(
                    jsonPath("errors[0].resource").value(
                        "/private/users/type/${query.type}?email=${query.email}"
                    )
                )
                .andExpect(jsonPath("errors[0].metadata.query_string").value(""))
                .andExpect(jsonPath("errors[0].message").value("user $email not found"))

            verify(exactly = 1) {
                toListUsersFilterByQueryMapper.mapFrom(
                    type = MERCHANT,
                    email = email,
                    customerId = null,
                    merchantId = null,
                    limit = null,
                    next = null
                )
            }
            verify(exactly = 1) { listUsersFilterByPortIn.list(query) }
            verify(exactly = 0) { toListUsersResponseMapper.mapFrom(any()) }
        }

        feature("set user password") {
            val aSetUserPassword = aSetUserPassword()

            val setUserPasswordRequest = SetUserPasswordRequest(
                userType = UserType.SUPPORT,
                email = email,
                password = password,
                permanent = false
            )

            scenario("successful set user password") {

                every { httpServletRequest.requestURI } returns "/private/users/set-password"
                every { toSetUserPasswordMapper.mapFrom(setUserPasswordRequest) } returns aSetUserPassword
                every { setUserPasswordUseCase.setPassword(aSetUserPassword) } returns Unit.right()

                mockMvc
                    .perform(
                        post("/private/users/set-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(aJsonRequestSetUserPassword())
                    )
                    .andExpect(status().isNoContent)

                verify(exactly = 1) { setUserPasswordUseCase.setPassword(aSetUserPassword) }
            }
        }
    }
})

fun aJsonRequestCreateNewUser() = """
    {
       "user_type": "MERCHANT",
       "attributes": {
             "email": "$email",
             "merchant_id": "$merchantId",
             "customer_id": "$customerId"
          }
    }
""".trimIndent()

fun aJsonRequestSetUserPassword() = """
    {
       "user_type": "SUPPORT",
       "email": "$email",
       "password": "$password",
       "permanent": false
    }
""".trimIndent()

fun aJsonRequestConfirmPasswordUser() = """
    {
       "code": "code",
       "password": "$password"
    }
""".trimIndent()

fun aControllerAdvice(request: HttpServletRequest) = ErrorHandler(
    errorResponseProvider = ErrorResponseProvider(
        currentResourceProvider = CurrentResourceProvider(request),
        metadataProvider = ErrorResponseMetadataProvider(
            currentResourceProvider = CurrentResourceProvider(request)
        )
    )
)
