plugins {
    kotlin("multiplatform")
    application
    id("com.squareup.sqldelight")
}

sqldelight {
    database("Database") {
        packageName = "com.well.server.db"
        dialect = "mysql"
    }
}

val ktorVersion = "1.4.0"
val sqldelightVersion = "1.4.4"

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("io.ktor:ktor-websockets:$ktorVersion")

                implementation("com.squareup.sqldelight:runtime-jvm:$sqldelightVersion")
                implementation("com.squareup.sqldelight:jdbc-driver:$sqldelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqldelightVersion")
                implementation("com.zaxxer:HikariCP:3.4.5")
                implementation("com.h2database:h2:1.4.200")
            }
        }
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass.get()
            )
        )
    }
}