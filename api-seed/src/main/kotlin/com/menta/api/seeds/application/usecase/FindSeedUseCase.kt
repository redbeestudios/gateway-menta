package com.menta.api.seeds.application.usecase

import com.menta.api.seeds.application.port.`in`.FindSeedPortIn
import com.menta.api.seeds.application.port.out.FindSeedPortOut
import com.menta.api.seeds.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindSeedUseCase(
    private val findSeedPortOut: FindSeedPortOut
) : FindSeedPortIn {

    companion object : CompanionLogger()
}
