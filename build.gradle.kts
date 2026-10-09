plugins {
    `java-library`
    id("fixture.conventions")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val directSecurity by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}
val transitiveSecurity by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation("commons-codec:commons-codec:1.16.0")
    implementation(libs.slf4j.api)
    compileOnly(libs.jetbrains.annotations)
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.0")
    directSecurity("org.apache.commons:commons-text:1.9")
    transitiveSecurity("org.apache.commons:commons-configuration2:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}

// Exercise both graphs during checks without evaluating any untrusted input.
tasks.register("resolveSecurityFixtures") {
    doLast {
        check(directSecurity.files.isNotEmpty())
        check(transitiveSecurity.files.isNotEmpty())
    }
}
tasks.named("check") {
    dependsOn("resolveSecurityFixtures")
}

apply(from = "declared-lower.gradle.kts")
