package ru.monke

import io.ktor.server.application.*
import ru.monke.api.configureStaticFiles

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureSerialization()
    configureDatabases()
    configureTemplating()
    configureStaticFiles()
    configureRouting()
}
