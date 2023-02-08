package com.menta.api.terminals.adapter.`in`.model.mapper

import com.menta.api.terminals.adapter.`in`.model.hateos.TerminalModelAssembler
import com.menta.api.terminals.domain.Terminal
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.stereotype.Component

@Component
class ToPagedTerminalMapper(
    private val pagedResourcesAssembler: PagedResourcesAssembler<Terminal>,
    private val terminalModelAssembler: TerminalModelAssembler,
) {
    fun map(pageTerminal: Page<Terminal>) =
        pagedResourcesAssembler.toModel(pageTerminal, terminalModelAssembler)
}