package com.menta.api.login.challenge

import com.menta.api.login.aUserPool
import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.adapter.`in`.model.NewPasswordRequiredChallengeRequest
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.domain.UserType.CUSTOMER
import com.menta.api.login.shared.domain.UserType.MERCHANT
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool

fun aChallengeSolution() =
    ChallengeSolution(
        userType = MERCHANT,
        challengeName = NEW_PASSWORD_REQUIRED,
        session = "a session",
        attributes = mapOf(
            NEW_PASSWORD to "a new password",
            USERNAME to "a username"
        )
    )

fun aUserPoolAwareChallengeSolution(
    userPool: UserPool = aUserPool(),
    solution: ChallengeSolution = aChallengeSolution()
) =
    UserPoolAwareChallengeSolution(
        userPool = userPool,
        solution = solution
    )

fun aChallengeSolutionRequest() =
    ChallengeSolutionRequest(
        userType = CUSTOMER,
        challengeName = NEW_PASSWORD_REQUIRED,
        session = "a session",
        attributes = mapOf(
            USERNAME to "user@user.com",
            NEW_PASSWORD to "a new password"
        )
    )

fun aNewPasswordRequiredChallengeRequest() =
    NewPasswordRequiredChallengeRequest(
        session = "a session",
        user = "user@user.com",
        userType = CUSTOMER,
        newPassword = "a new password"
    )