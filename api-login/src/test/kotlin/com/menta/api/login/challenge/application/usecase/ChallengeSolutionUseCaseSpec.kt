package com.menta.api.login.challenge.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.login.aUserAuthWithChallenge
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.aUserPool
import com.menta.api.login.challenge.aChallengeSolution
import com.menta.api.login.challenge.aUserPoolAwareChallengeSolution
import com.menta.api.login.challenge.application.port.out.ChallengeSolutionOutPort
import com.menta.api.login.challenge.domain.mapper.ToUserPoolAwareChallengeSolutionMapper
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

class ChallengeSolutionUseCaseSpec : FeatureSpec({

    val challengeSolutionOutPort = mockk<ChallengeSolutionOutPort>()
    val userPoolProvider = mockk<UserPoolProvider>()
    val toUserPoolAwareChallengeSolution = mockk<ToUserPoolAwareChallengeSolutionMapper>()

    val useCase = ChallengeSolutionUseCase(
        challengeSolutionOutPort = challengeSolutionOutPort,
        userPoolProvider = userPoolProvider,
        toUserPoolAwareChallengeSolution = toUserPoolAwareChallengeSolution
    )

    beforeEach { clearAllMocks() }

    feature("solve challenge") {

        scenario("user pool error") {
            val solution = aChallengeSolution()
            val error = missingConfigurationForUserType(solution.userType)

            every { userPoolProvider.provideFor(solution.userType) } returns error.left()

            useCase.solveChallenge(solution) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(solution.userType) }
            verify(exactly = 0) { toUserPoolAwareChallengeSolution.mapFrom(any(), any()) }
            verify(exactly = 0) { challengeSolutionOutPort.solve(any()) }
        }

        scenario("error solving challenge") {
            val solution = aChallengeSolution()
            val userPool = aUserPool()
            val userPoolAwareSolution = aUserPoolAwareChallengeSolution()
            val error = unauthorizedUser()

            every { userPoolProvider.provideFor(solution.userType) } returns userPool.right()
            every { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) } returns userPoolAwareSolution
            every { challengeSolutionOutPort.solve(userPoolAwareSolution) } returns error.left()

            useCase.solveChallenge(solution) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(solution.userType) }
            verify(exactly = 1) { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) }
            verify(exactly = 1) { challengeSolutionOutPort.solve(userPoolAwareSolution) }
        }

        scenario("challenge solved and token retrieved") {
            val solution = aChallengeSolution()
            val userPool = aUserPool()
            val userPoolAwareSolution = aUserPoolAwareChallengeSolution()
            val userAuth = aUserAuthWithToken()

            every { userPoolProvider.provideFor(solution.userType) } returns userPool.right()
            every { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) } returns userPoolAwareSolution
            every { challengeSolutionOutPort.solve(userPoolAwareSolution) } returns userAuth.right()

            useCase.solveChallenge(solution) shouldBeRight userAuth

            verify(exactly = 1) { userPoolProvider.provideFor(solution.userType) }
            verify(exactly = 1) { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) }
            verify(exactly = 1) { challengeSolutionOutPort.solve(userPoolAwareSolution) }
        }

        scenario("challenge solved and new challenge retrieved") {
            val solution = aChallengeSolution()
            val userPool = aUserPool()
            val userPoolAwareSolution = aUserPoolAwareChallengeSolution()
            val userAuth = aUserAuthWithChallenge()

            every { userPoolProvider.provideFor(solution.userType) } returns userPool.right()
            every { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) } returns userPoolAwareSolution
            every { challengeSolutionOutPort.solve(userPoolAwareSolution) } returns userAuth.right()

            useCase.solveChallenge(solution) shouldBeRight userAuth

            verify(exactly = 1) { userPoolProvider.provideFor(solution.userType) }
            verify(exactly = 1) { toUserPoolAwareChallengeSolution.mapFrom(userPool, solution) }
            verify(exactly = 1) { challengeSolutionOutPort.solve(userPoolAwareSolution) }
        }

    }
})