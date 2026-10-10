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
    implementation("commons-codec:commons-codec:1.22.1")
    implementation(libs.slf4j.api)
    compileOnly(libs.jetbrains.annotations)
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.4")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.4")
    directSecurity("org.apache.commons:commons-text:1.15.0")
    transitiveSecurity("org.apache.commons:commons-configuration2:2.15.1")
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
