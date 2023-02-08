import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("jacoco")
    id("org.sonarqube") version "3.3"
}

group = "com.kiwi"
version = "0.0.2"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = "2021.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

    implementation("junit:junit:4.13.2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    //Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //Documentation
    implementation("org.springdoc:springdoc-openapi-ui:1.6.4")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.4")


    //Test
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // Arch unit
    testImplementation("com.tngtech.archunit:archunit:0.22.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.22.0")
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
        property(
            "sonar.coverage.exclusions",
            "src/main/kotlin/com/kiwi/api/reverse/config/**, "
        )
    }
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

springBoot {
    buildInfo()
}
