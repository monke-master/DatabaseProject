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
                        h1(classes = "mb-4") { +"Регистрация" }
                        form(action = "/sign_up", method = FormMethod.post, classes = "needs-validation") {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Логин:" }
                                textInput(name = "login") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Пароль:" }
                                passwordInput(name = "password") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") {
                                +"Зарегестрироваться"
                            }
                        }

                        div(classes = "mt-3") {
                            a(href = "/sign_in", classes = "btn btn-secondary") {
                                +"У меня уже есть аккаунт"
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
            UserSession.currentUser = newPlayer
            call.respondRedirect("/select_entity")
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
                                label(classes = "form-label") { +"Логин:" }
                                textInput(name = "login") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Пароль:" }
                                passwordInput(name = "password") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary") {
                                +"Войти"
                            }
                        }

                        div(classes = "mt-3") {
                            a(href = "/sign_up", classes = "btn btn-secondary") {
                                +"Создать аккаунт"
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
                UserSession.currentUser = player
                call.respondRedirect("/select_entity")
            } else {
                call.respondHtml(HttpStatusCode.Unauthorized) {
                    head {
                        title("Login Error")
                        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
                    }
                    body {
                        div("container mt-5") {
                            div("card p-4 bg-light") {
                                h1("text-danger") { +"Invalid Credentials" }
                                p { +"The login or password you entered is incorrect. Please try again." }
                                a(href = "/sign_in", classes = "btn btn-primary mt-2") { +"Go back to login" }
                            }
                        }
                    }
                }
            }
        }
    }
}
