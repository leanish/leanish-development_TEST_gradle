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
