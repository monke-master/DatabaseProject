package ru.monke.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*

fun Application.entityRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        get("/entities/{type}") {
            val entityType = call.parameters["type"] ?: return@get call.respondText(
                "Entity type is required", status = HttpStatusCode.BadRequest
            )

            val entities = when (entityType) {
                "city" -> cityDatastore.getAllCities()
                "unit" -> unitDatastore.getAllUnits()
                "building" -> buildingDatastore.getAllBuildings()
                "district" -> districtDatastore.getAllDistricts()
                else -> return@get call.respondText(
                    "Unknown entity type", status = HttpStatusCode.BadRequest
                )
            }

            call.respondHtml {
                entityListPage(entityType, entities)
            }
        }
    }
}

fun HTML.entityListPage(entityType: String, entities: List<Any>) {
    head {
        title { +"Entity List - $entityType" }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
    }
    body {
        div(classes = "container my-4") {
            h1(classes = "mb-4") { +"List of ${entityType.capitalize()}" }

            // Show "Create" button if the user is an admin
            if (UserSession.currentUser?.isAdmin == true) {
                a(
                    href = "/create_entity/$entityType",
                    classes = "btn btn-success mb-4"
                ) {
                    +"Create $entityType"
                }
            }

            div(classes = "row row-cols-1 row-cols-md-3 g-4") {
                entities.forEach { entity ->
                    div(classes = "col") {
                        div(classes = "card h-100 text-center") {
                            // Display image if available
                            img(
                                classes = "card-img-top mx-auto d-block",
                                src = "/static/images/$entityType.png",
                                alt = "$entityType Image"
                            ) {
                                attributes["style"] = "width: 40%; height: auto;"
                            }
                            // Display entity fields dynamically
                            div(classes = "card-body") {
                                h5(classes = "card-title") {
                                    when (entity) {
                                        is ExposedCity -> +entity.name
                                        is ExposedUnit -> +entity.name
                                        is ExposedBuilding -> +entity.name
                                        is ExposedDistrict -> +"District #${entity.name}"
                                    }
                                }
                                p(classes = "card-text") {
                                    when (entity) {
                                        is ExposedCity -> +"Population: ${entity.population}"
                                        is ExposedUnit -> +"Damage: ${entity.damage}, Health: ${entity.health}"
                                        is ExposedBuilding -> +"Production: ${entity.production}, Defense: ${entity.defense}"
                                        is ExposedDistrict -> +"Production Cost: ${entity.productionCost}"
                                    }
                                }
                                a(href = "/details/$entityType/${getId(entity)}", classes = "btn btn-primary") {
                                    +"View Details"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getId(entity: Any): Int = when (entity) {
    is ExposedCity -> entity.id
    is ExposedUnit -> entity.id
    is ExposedBuilding -> entity.id
    is ExposedDistrict -> entity.id
    else -> throw IllegalArgumentException("Unknown entity type")
}
