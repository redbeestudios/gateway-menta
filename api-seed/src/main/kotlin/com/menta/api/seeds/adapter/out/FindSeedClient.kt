package com.menta.api.seeds.adapter.out

import com.menta.api.seeds.application.port.out.FindSeedPortOut
import com.menta.api.seeds.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindSeedClient : FindSeedPortOut {

    companion object : CompanionLogger()
}
