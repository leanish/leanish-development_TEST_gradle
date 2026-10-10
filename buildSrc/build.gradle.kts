plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "6.7.11"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
