package ru.monke.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*

fun Application.creatingRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        get("/create_entity/{type}") {
            val entityType = call.parameters["type"] ?: return@get call.respondText(
                "Entity type is required", status = HttpStatusCode.BadRequest
            )

            when (entityType) {
                "city" -> call.respondRedirect("/create_city")
                "unit" -> call.respondRedirect("/create_unit")
                "building" -> call.respondRedirect("/create_building")
                "district" -> call.respondRedirect("/create_district")
                else -> return@get call.respondText(
                    "Unknown entity type", status = HttpStatusCode.BadRequest
                )
            }
        }
        get("/create_city") {
            call.respondHtml {
                head {
                    title { +"Create City" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New City" }

                        form(action = "/create_city", method = FormMethod.post) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Player ID:" }
                                textInput(name = "playerId") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter Player ID"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter City Name"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Population:" }
                                numberInput(name = "population") {
                                    classes = setOf("form-control")
                                    placeholder = "Enter Population"
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create City"
                            }
                        }
                    }
                }
            }
        }

        get("/create_district") {
            call.respondHtml {
                head {
                    title { +"Create District" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New District" }

                        form(action = "/create_district", method = FormMethod.post) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City ID:" }
                                textInput(name = "cityId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Название:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                    placeholder = "Введите название района"
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create District"
                            }
                        }
                    }
                }
            }
        }

        get("/create_building") {
            call.respondHtml {
                head {
                    title { +"Create Building" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New Building" }

                        form(action = "/create_building", method = FormMethod.post) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"District ID:" }
                                textInput(name = "districtId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"City ID:" }
                                textInput(name = "cityId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Building Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production:" }
                                numberInput(name = "production") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Food:" }
                                numberInput(name = "food") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Gold:" }
                                numberInput(name = "gold") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Defense:" }
                                numberInput(name = "defense") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Description:" }
                                textArea {
                                    classes = setOf("form-control")
                                }
                            }

                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create Building"
                            }
                        }
                    }
                }
            }
        }

        get("/create_unit") {
            call.respondHtml {
                head {
                    title { +"Create Unit" }
                    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
                }
                body {
                    div(classes = "container mt-5") {
                        h1(classes = "mb-4") { +"Create New Unit" }

                        form(action = "/create_unit", method = FormMethod.post) {
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Player ID:" }
                                textInput(name = "playerId") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Unit Name:" }
                                textInput(name = "name") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Damage:" }
                                numberInput(name = "damage") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Health:" }
                                numberInput(name = "health") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Movement:" }
                                numberInput(name = "movement") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Production Cost:" }
                                numberInput(name = "productionCost") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Содержание:" }
                                numberInput(name = "salary") {
                                    classes = setOf("form-control")
                                }
                            }
                            div(classes = "mb-3") {
                                label(classes = "form-label") { +"Description:" }
                                textArea {
                                    classes = setOf("form-control")
                                }
                            }
                            button(type = ButtonType.submit, classes = "btn btn-primary mt-3") {
                                +"Create Unit"
                            }
                        }
                    }
                }
            }
        }


        post("/create_unit") {
            val params = call.receiveParameters()
            val playerId = params["playerId"] ?: return@post call.respondText("Player ID is required", status = HttpStatusCode.BadRequest)
            val name = params["name"] ?: return@post call.respondText("Unit name is required", status = HttpStatusCode.BadRequest)
            val damage = params["damage"]?.toIntOrNull() ?: return@post call.respondText("Valid damage is required", status = HttpStatusCode.BadRequest)
            val health = params["health"]?.toIntOrNull() ?: return@post call.respondText("Valid health is required", status = HttpStatusCode.BadRequest)
            val movement = params["movement"]?.toIntOrNull() ?: return@post call.respondText("Valid movement is required", status = HttpStatusCode.BadRequest)
            val productionCost = params["productionCost"]?.toIntOrNull() ?: return@post call.respondText("Valid production cost is required", status = HttpStatusCode.BadRequest)
            val salary = params["salary"]?.toIntOrNull() ?: return@post call.respondText("Valid production cost is required", status = HttpStatusCode.BadRequest)
            val description = params["description"] ?: ""

            try {
                unitDatastore.create(
                    ExposedUnit(
                        playerId = playerId.toInt(),
                        name = name,
                        damage = damage,
                        health = health,
                        movement = movement,
                        productionCost = productionCost,
                        description = description,
                        salary = salary
                    )
                )
                call.respondRedirect("/entities/unit")
            } catch (e: Exception) {
                call.respondText("Failed to create unit: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }




        post("/create_city") {
            try {
                val params = call.receiveParameters()
                val playerId =params["playerId"]
                    ?: return@post call.respondText("Player ID is required", status = HttpStatusCode.BadRequest)
                val name = params["name"]
                    ?: return@post call.respondText("City name is required", status = HttpStatusCode.BadRequest)
                val population = params["population"]?.toIntOrNull()
                    ?: return@post call.respondText("Valid population is required", status = HttpStatusCode.BadRequest)

                cityDatastore.create(ExposedCity(playerId = playerId.toInt(), name =  name, population = population))
                call.respondRedirect("/entities/city")
            } catch (e: Exception) {
                call.respondText("Failed to create city: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/create_district") {
            val params = call.receiveParameters()
            val cityId = params["cityId"] ?: return@post call.respondText("City ID is required", status = HttpStatusCode.BadRequest)
            val productionCost = params["productionCost"]?.toIntOrNull() ?: return@post call.respondText("Valid production cost is required", status = HttpStatusCode.BadRequest)
            val name = params["name"] ?: return@post call.respondText("Valid population is required", status = HttpStatusCode.BadRequest)

            try {
                districtDatastore.create(
                    ExposedDistrict(
                        cityId = cityId.toInt(),
                        productionCost = productionCost,
                        name = name
                    )
                )
                call.respondRedirect("/entities/district")
            } catch (e: Exception) {
                call.respondText("Failed to create district: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/create_building") {
            val params = call.receiveParameters()
            val districtId = params["districtId"] ?: return@post call.respondText("District ID is required", status = HttpStatusCode.BadRequest)
            val cityId = params["cityId"] ?: return@post call.respondText("City ID is required", status = HttpStatusCode.BadRequest)
            val name = params["name"] ?: return@post call.respondText("Building name is required", status = HttpStatusCode.BadRequest)
            val productionCost = params["productionCost"]?.toIntOrNull() ?: return@post call.respondText("Valid production cost is required", status = HttpStatusCode.BadRequest)
            val production = params["production"]?.toIntOrNull() ?: return@post call.respondText("Valid production cost is required", status = HttpStatusCode.BadRequest)

            val food = params["food"]?.toIntOrNull() ?: return@post call.respondText("Valid food value is required", status = HttpStatusCode.BadRequest)
            val gold = params["gold"]?.toIntOrNull() ?: return@post call.respondText("Valid gold value is required", status = HttpStatusCode.BadRequest)
            val defense = params["defense"]?.toIntOrNull() ?: return@post call.respondText("Valid defense value is required", status = HttpStatusCode.BadRequest)
            val description = params["description"] ?: ""

            try {
                buildingDatastore.create(
                    ExposedBuilding(
                        districtId = districtId.toInt(),
                        cityId = cityId.toInt(),
                        name = name,
                        productionCost = productionCost,
                        food = food,
                        gold = gold,
                        defense = defense,
                        description = description,
                        production = production
                    )
                )
                call.respondRedirect("/entities/building")
            } catch (e: Exception) {
                call.respondText("Failed to create building: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }


    }
}