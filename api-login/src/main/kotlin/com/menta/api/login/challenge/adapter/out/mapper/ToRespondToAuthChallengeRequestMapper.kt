package com.menta.api.login.challenge.adapter.out.mapper

import com.amazonaws.services.cognitoidp.model.ChallengeNameType
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest
import com.menta.api.login.challenge.domain.ChallengeName
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToRespondToAuthChallengeRequestMapper {

    fun mapFrom(userPoolAwareSolution: UserPoolAwareChallengeSolution): RespondToAuthChallengeRequest =
        with(userPoolAwareSolution) {
            RespondToAuthChallengeRequest()
                .withClientId(userPool.clientId)
                .withChallengeName(solution.challengeName.asCognitoChallengeNameType())
                .withSession(solution.session)
                .withChallengeResponses(solution.attributes.mapKeys { it.key.name })
        }
            .log { info("RespondToAuthChallengeRequest mapped: {}", it) }

    private fun ChallengeName.asCognitoChallengeNameType() =
        ChallengeNameType.valueOf(name)

    companion object : CompanionLogger()
}