package com.menta.apisecrets.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTER = "Adapter"
private const val APPLICATION = "Application"
private const val SHARED = "Shared"
private const val USECASE = "UseCase"
private const val PORTIN = "Por In"

@AnalyzeClasses(packages = ["com.menta.apisecrets"], importOptions = [ImportOption.DoNotIncludeTests::class])
class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(SHARED).definedBy("com.menta.apisecrets.shared..")
        .layer(DOMAIN).definedBy("com.menta.apisecrets.domain..")
        .layer(ADAPTER).definedBy("com.menta.apisecrets.adapter..")
        .layer(APPLICATION).definedBy("com.menta.apisecrets.application..")
        .layer(USECASE).definedBy("com.menta.apisecrets.application.usecase")
        .layer(PORTIN).definedBy("com.menta.apisecrets.application.port.in")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTER, SHARED)
        .whereLayer(ADAPTER).mayOnlyBeAccessedByLayers(SHARED)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTER, SHARED)
        .whereLayer(USECASE).mayOnlyBeAccessedByLayers(PORTIN)
}
