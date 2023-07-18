plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.kotlin.kapt") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.1"
    id("org.sonarqube") version "4.3.0.3225"
}

version = "0.1"
group = "de.sambalmueslie.discord.bot"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    compileOnly("org.graalvm.nativeimage:svm")

    implementation("io.micronaut:micronaut-validation")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // discord
    implementation("com.discord4j:discord4j-core:3.2.5")
    // mockk
    testImplementation("io.mockk:mockk:1.13.5")
    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2")
    // velocity
//    implementation("org.apache.velocity:velocity-engine-core:2.3")
//    implementation("org.apache.velocity:velocity-tools:2.0")

}


application {
    mainClass.set("de.sambalmueslie.discord.bot.staffsergeant.StaffSergeantApplication")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("de.sambalmueslie.discord.bot.staffsergeant.*")
    }
}


tasks.jar {
    manifest {
        attributes(
            mapOf("Main-Class" to "de.sambalmueslie.discord.bot.staffsergeant.StaffSergeantApplication")
        )
    }
}

tasks.register("stage") {
    dependsOn("build", "clean")
}


sonarqube {
    properties {
        property("sonar.projectKey", "Black-Forrest-Development_staffseargant")
        property("sonar.organization", "black-forrest-development")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}




