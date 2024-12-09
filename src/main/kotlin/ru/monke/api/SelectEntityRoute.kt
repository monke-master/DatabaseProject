package ru.monke.api

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.entitySelectionRoutes() {
    routing {
        get("/select_entity") {
            call.respondHtml {
                head {
                    title { +"Select Entity" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4 text-center") { +"Choose an Entity" }

                        // Cards grid for entities
                        div(classes = "row row-cols-1 row-cols-md-2 g-4") {
                            entityCard("City", "/static/city.png", "/entities/city")
                            entityCard("Unit", "/static/unit.png", "/entities/unit")
                            entityCard("Building", "/static/building.png", "/entities/building")
                            entityCard("District", "/static/district.png", "/entities/district")
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.entityCard(entityName: String, imagePath: String, link: String) {
    div(classes = "col") {
        div(classes = "card h-100 text-center") {
            img(
                classes = "card-img-top mx-auto d-block",
                src = imagePath,
                alt = "$entityName Image"
            ) {
                attributes["style"] = "width: 40%; height: auto;"
            }
            div(classes = "card-body") {
                h5(classes = "card-title") { +entityName }
                a(href = link, classes = "btn btn-primary mt-2") {
                    +"View $entityName"
                }
            }
        }
    }
}
