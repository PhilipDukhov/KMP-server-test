package com.well.server

import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.well.server.db.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.http.*
import org.slf4j.event.*
import io.ktor.routing.*
import java.io.File

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.toString())
        }
    }

    val database = initialiseDatabase(this)

    routing {
        post("/") {
            val userId: Int = database.userQueries.run {
                val facebookId = call.receive<String>()
                transactionWithResult {
                    getByFacebookId(facebookId)
                        .executeAsOneOrNull()
                        ?: run {
                            insertFacebook(
                                "name",
                                "last",
                                facebookId
                            )
                            println(lastInsertId().executeAsList())
                            lastInsertId()
                                .executeAsOne()
                                .toInt()
                        }
                }
            }
            call.respondText("HELLO WORLD! $userId")
        }
    }
}

fun initialiseDatabase(app: Application): Database {
    val dbConfig = app.environment.config.config("database")
    var connectionUrl = dbConfig.property("connection").getString()

    // If this is a local h2 database, ensure the directories exist
    if (connectionUrl.startsWith("jdbc:h2:file:")) {
        val dbFile = File(connectionUrl.removePrefix("jdbc:h2:file:")).absoluteFile
        if (!dbFile.parentFile.exists()) {
            dbFile.parentFile.mkdirs()
        }
        connectionUrl = "jdbc:h2:file:${dbFile.absolutePath}"
    }

    val datasourceConfig = HikariConfig().apply {
        jdbcUrl = connectionUrl
        dbConfig.propertyOrNull("username")?.getString()?.let(this::setUsername)
        dbConfig.propertyOrNull("password")?.getString()?.let(this::setPassword)
        dbConfig.propertyOrNull("poolSize")?.getString()?.toInt()?.let(this::setMaximumPoolSize)
    }
    val dataSource = HikariDataSource(datasourceConfig)
    val driver = dataSource.asJdbcDriver()
    Database.Schema.create(driver)
    val db = Database(driver)
    app.environment.monitor.subscribe(ApplicationStopped) { driver.close() }

    return db
}

