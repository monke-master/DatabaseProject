package ru.monke

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import ru.monke.api.authRoutesWithBootstrap
import ru.monke.api.entitySelectionRoutes
import ru.monke.database.PlayerDatastore

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    val database = Database.connect("jdbc:h2:./data", driver = "org.h2.Driver")
    val playerDatastore = PlayerDatastore(database)
    authRoutesWithBootstrap(playerDatastore)
    entitySelectionRoutes()
}
