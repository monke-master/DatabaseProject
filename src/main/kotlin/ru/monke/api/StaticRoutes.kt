package ru.monke.api

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureStaticFiles() {
    routing {
        static("/static") {
            resources("static") // This serves all files in `resources/static`
        }
        static("/uploaded_photos") {
            files("uploaded_photos") // This serves all files in `resources/static`
        }
    }
}