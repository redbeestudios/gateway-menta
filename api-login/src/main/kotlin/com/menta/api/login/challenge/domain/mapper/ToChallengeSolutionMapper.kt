package com.menta.api.login.challenge.domain.mapper

import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToChallengeSolutionMapper {

    fun mapFrom(request: ChallengeSolutionRequest) =
        with(request) {
            ChallengeSolution(
                userType = userType,
                challengeName = challengeName,
                session = session,
                attributes = attributes
            )
        }.log { info("challenge solution mapped: {}", it) }

    companion object : CompanionLogger()
}