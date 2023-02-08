package com.kiwi.api.reverse.hexagonal.application.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val CONFIG = "Config"

class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(CONFIG).definedBy("com.kiwi.api.reverse.config..")
        .layer(DOMAIN).definedBy("com.kiwi.api.reverse.hexagonal.domain..")
        .layer(ADAPTERS).definedBy("com.kiwi.api.reverse.adapter..")
        .layer(APPLICATION).definedBy("com.kiwi.api.reverse.hexagonal.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG)
}
