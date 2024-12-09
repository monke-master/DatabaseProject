package ru.monke.api


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.PlayerDatastore
import ru.monke.database.ExposedPlayer

fun Application.authRoutesWithBootstrap(playerDatastore: PlayerDatastore) {
    routing {
        // Sign-up page
        get("/sign_up") {
            call.respondHtml {
                head {
                    title { +"Sign Up" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Sign Up" }
                        form(action = "/sign_up", method = FormMethod.post, classes = "needs-validation") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Login:" }
                                textInput(name = "login") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Password:" }
                                passwordInput(name = "password") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "form-check mb-3") {
                                checkBoxInput(name = "isAdmin") {
                                    classes = setOf("form-check-input")
                                }
                                label(classes = "form-check-label") {
                                    +"Admin"
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") {
                                +"Sign Up"
                            }
                        }
                    }
                }
            }
        }

        post("/sign_up") {
            val params = call.receiveParameters()
            val login = params["login"] ?: return@post call.respondText("Missing login", status = HttpStatusCode.BadRequest)
            val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)
            val isAdmin = params["isAdmin"]?.toBoolean() ?: false

            val newPlayer = ExposedPlayer(login, password, isAdmin)
            val id = playerDatastore.create(newPlayer)
            call.respondText("User created with ID: $id")
        }

        // Sign-in page
        get("/sign_in") {
            call.respondHtml {
                head {
                    title { +"Sign In" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Sign In" }
                        form(action = "/sign_in", method = FormMethod.post, classes = "needs-validation") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Login:" }
                                textInput(name = "login") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Password:" }
                                passwordInput(name = "password") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") {
                                +"Sign In"
                            }
                        }
                    }
                }
            }
        }

        post("/sign_in") {
            val params = call.receiveParameters()
            val login = params["login"] ?: return@post call.respondText("Missing login", status = HttpStatusCode.BadRequest)
            val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

            // Check credentials
            val player = playerDatastore.read(login)

            if (player != null && player.password == password) {
                call.respondText("Welcome, ${player.login}!")
            } else {
                call.respondText("Invalid credentials", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}
