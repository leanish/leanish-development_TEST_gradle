buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20") {
            because("GHSA-r937-wjx7-w2jp: replace vulnerable Kotlin Gradle plugin brought by Kotlin DSL")
        }
    }
}

plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "6.0.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
