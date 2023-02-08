import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    `java-library`
    `maven-publish`
    id("org.asciidoctor.convert") version "1.5.8"
    id("jacoco")
    id("org.sonarqube") version "3.3"
}

group = "com.menta"
version = "0.3.3"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val ktlint by configurations.creating

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = "2021.0.0"

dependencies {

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Ktlint
    ktlint("com.pinterest:ktlint:0.40.0")

    // Dev
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.2.4")

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
            "src/main/kotlin/com/menta/libs/security/ownership/filter/configuration/**, " +
                    "src/main/kotlin/com/menta/libs/security/ownership/identity/extractor/configuration/**, " +
                    "src/main/kotlin/com/menta/libs/security/ownership/identity/provider/configuration/**, " +
                    "src/main/kotlin/com/menta/libs/security/ownership/identity/validator/configuration/**, " +
                    "src/main/kotlin/com/menta/libs/security/ownership/owner/provider/configuration/**, " +
                    "src/main/kotlin/com/menta/libs/security/ownership/owner/validator/configuration/**, " 
        )
    }
}

tasks.sonarqube {
    dependsOn(tasks.check)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks.withType<Test> {
    useJUnitPlatform() {
        includeEngines("kotest")
        includeEngines("archunit")
    }
}

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

publishing {

    /**
     *  Las variables de entorno que se usan en esta configuración son las que settea el pipeline de CI/CD
     */

    publications {
        create<MavenPublication>(rootProject.name) {
            version = "${project.version}-${System.getProperty("PUBLISH_TARGET")}"
            pom {
                name.set(rootProject.name)
                description.set("facilitar el manejo de seguridad con multiples issuers")
                developers {
                    developer {
                        id.set("sbasmz")
                        name.set("Sebastian Martinez")
                        email.set("sebastian.martinez@redbee.io")
                    }
                }
                scm {
                    url.set("https://git.menta.global/backend/libs/libs-security")
                }
            }
            artifact(sourcesJar)
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri(System.getProperty("PUBLISH_REPOSITORY") ?: "")
            credentials {
                username = System.getProperty("PUBLISH_USERNAME")
                password = System.getProperty("PUBLISH_PASSWORD")
            }
        }
    }
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    jar {
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
                )
            )
        }
    }

    compileJava {
        inputs.files(processResources)
    }

    bootJar {
        enabled = false
    }

    jar {
        enabled = true
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