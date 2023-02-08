package com.menta.api.seeds.adapter.`in`

import com.menta.api.seeds.application.port.`in`.FindSeedPortIn
import com.menta.api.seeds.shared.other.util.log.CompanionLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/private/seeds")
class SeedController(
    private val findSeed: FindSeedPortIn
) {

    companion object : CompanionLogger()
}
