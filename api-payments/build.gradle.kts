import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("jacoco")
    id("org.sonarqube") version "3.3"
    id("au.com.dius.pact") version "4.1.10"
}

group = "com.kiwi"
version = "0.17.1"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val ktlint by configurations.creating

repositories {
    mavenCentral()

    maven {
        url = uri("https://nexus.tools.menta.global/repository/maven-releases/")
        credentials {
            username = "developers"
            password = "ruu6Jeip2IeYaa"
        }
    }
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = "2021.0.0"

dependencies {

    // Spring
    implementation("org.springframework:spring-web:5.3.16")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.6.6")
    implementation("org.springframework.kafka:spring-kafka:2.8.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Menta Security
    implementation("com.menta:libs-security:0.0.2-RELEASE")

    // Arrow
    implementation("io.arrow-kt:arrow-core:1.1.2")

    // BBDD
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.4")
    implementation("org.liquibase:liquibase-core:4.9.1")

    // Logs
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    // Documentation
    implementation("org.springdoc:springdoc-openapi-ui:1.6.4")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.4")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.9")

    // Elastic
    implementation("co.elastic.apm:apm-agent-attach:1.32.0")

    // Ktlint
    ktlint("com.pinterest:ktlint:0.40.0")

    // Dev
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")

    // Arch unit
    testImplementation("com.tngtech.archunit:archunit:0.14.1")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.14.1")

    //Pact
    testImplementation("au.com.dius.pact.consumer:junit5:4.1.7")
    testImplementation("au.com.dius:pact-jvm-consumer-java8:4.0.10") {
        exclude(group = "io.arrow-kt", module= "arrow-core-extensions")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(false)
        csv.required.set(false)
        xml.outputLocation.set(file("${buildDir}/jacoco/jacoco.xml"))
    }
}

sonarqube {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/jacoco/jacoco.xml")
        property("sonar.coverage.exclusions", "src/main/kotlin/com/kiwi/api/payments/shared/kafka/**")
        property("sonar.coverage.exclusions", "src/main/kotlin/com/kiwi/api/payments/config/** ")
    }
}

tasks.sonarqube {
    dependsOn(tasks.check)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform() {
        includeEngines("kotest")
        includeEngines("archunit")
    }
    useJUnitPlatform()
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacocoTest.exec"))
        classDumpDir = file("${buildDir}/jacoco/classpathdumps")
    }
    finalizedBy(tasks.jacocoTestReport)

    outputs.dir(snippetsDir)

    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events("PASSED", "FAILED", "SKIPPED")

    }
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
}

val ktlintCheck by tasks.creating(JavaExec::class) {
    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

ktlintCheck.dependsOn(ktlintFormat)
tasks.build.get().dependsOn(ktlintCheck)

springBoot {
    buildInfo()
}
