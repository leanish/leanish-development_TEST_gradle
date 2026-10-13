buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20") {
            because("GHSA-r937-wjx7-w2jp: kotlin-dsl 6.0.0 brought a vulnerable Kotlin Gradle plugin; this floor keeps it at the fixed 2.4.20 or newer")
        }
    }
}

plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "6.7.12"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
