package com.menta.api.terminals.domain.resolver

import com.menta.api.terminals.aTerminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldNotBe

class TerminalDeletionResolverSpec : FeatureSpec({

    val resolver = TerminalDeletionResolver()

    feature("resolveDeletion") {

        scenario("with terminal") {
            resolver.resolveDeletion(
                aTerminal
            ).deleteDate shouldNotBe null
        }
    }
})
