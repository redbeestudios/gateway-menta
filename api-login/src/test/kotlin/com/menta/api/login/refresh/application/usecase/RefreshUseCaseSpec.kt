package com.menta.api.login.refresh.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.aUserPool
import com.menta.api.login.refresh.aRefresh
import com.menta.api.login.refresh.aUserPoolAwareRefresh
import com.menta.api.login.refresh.application.port.`in`.RefreshOutPort
import com.menta.api.login.refresh.domain.mapper.ToUserPoolAwareRefreshMapper
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RefreshUseCaseSpec : FeatureSpec({

    val userPoolProvider = mockk<UserPoolProvider>()
    val toUserPoolAwareRefreshMapper = mockk<ToUserPoolAwareRefreshMapper>()
    val refreshOutPort = mockk<RefreshOutPort>()

    val useCase = RefreshUseCase(
        userPoolProvider = userPoolProvider,
        toUserPoolAwareRefreshMapper = toUserPoolAwareRefreshMapper,
        refreshOutPort = refreshOutPort
    )

    beforeEach { clearAllMocks() }

    feature("refresh token") {

        scenario("user pool not found") {
            val refresh = aRefresh()
            val error = missingConfigurationForUserType(refresh.userType)

            every { userPoolProvider.provideFor(refresh.userType) } returns error.left()

            useCase.refresh(refresh) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(refresh.userType) }
            verify(exactly = 0) { toUserPoolAwareRefreshMapper.mapFrom(any(), any()) }
            verify(exactly = 0) { refreshOutPort.refresh(any()) }
        }

        scenario("refresh failed") {
            val refresh = aRefresh()
            val userPool = aUserPool()
            val userPoolAwareRefresh = aUserPoolAwareRefresh()
            val error = unauthorizedUser()

            every { userPoolProvider.provideFor(refresh.userType) } returns userPool.right()
            every { toUserPoolAwareRefreshMapper.mapFrom(refresh, userPool) } returns userPoolAwareRefresh
            every { refreshOutPort.refresh(userPoolAwareRefresh) } returns error.left()

            useCase.refresh(refresh) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(refresh.userType) }
            verify(exactly = 1) { toUserPoolAwareRefreshMapper.mapFrom(any(), any()) }
            verify(exactly = 1) { refreshOutPort.refresh(any()) }
        }

        scenario("successful refresh") {
            val refresh = aRefresh()
            val userPool = aUserPool()
            val userPoolAwareRefresh = aUserPoolAwareRefresh()
            val userAuth = aUserAuthWithToken()

            every { userPoolProvider.provideFor(refresh.userType) } returns userPool.right()
            every { toUserPoolAwareRefreshMapper.mapFrom(refresh, userPool) } returns userPoolAwareRefresh
            every { refreshOutPort.refresh(userPoolAwareRefresh) } returns userAuth.right()

            useCase.refresh(refresh) shouldBeRight userAuth

            verify(exactly = 1) { userPoolProvider.provideFor(refresh.userType) }
            verify(exactly = 1) { toUserPoolAwareRefreshMapper.mapFrom(any(), any()) }
            verify(exactly = 1) { refreshOutPort.refresh(any()) }
        }


    }
}) {
}