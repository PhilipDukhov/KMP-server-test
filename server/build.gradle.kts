import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles

plugins {
    kotlin("multiplatform")
    application
}

val ktorVersion = "1.4.0"

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
            }
        }
    }
}