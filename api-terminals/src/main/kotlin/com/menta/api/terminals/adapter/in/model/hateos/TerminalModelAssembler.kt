package com.menta.api.terminals.adapter.`in`.model.hateos

import com.menta.api.terminals.adapter.`in`.controller.TerminalController
import com.menta.api.terminals.domain.Terminal
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class TerminalModelAssembler(
) : RepresentationModelAssembler<Terminal, TerminalModel> {
    override fun toModel(entity: Terminal): TerminalModel =
        with(entity) {
            TerminalModel(
                id = id,
                merchantId = merchantId,
                customerId = customerId,
                serialCode = serialCode,
                hardwareVersion = hardwareVersion,
                tradeMark = tradeMark,
                model = model,
                status = status,
                deleteDate = deleteDate,
                features = features
            ).add(
                linkTo(methodOn(TerminalController::class.java)
                    .getBy(id))
                    .withSelfRel())
        }

}