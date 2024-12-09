package ru.monke.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import ru.monke.database.*

fun Application.detailsRoutes(
    cityDatastore: CityDatastore,
    unitDatastore: UnitDatastore,
    buildingDatastore: BuildingDatastore,
    districtDatastore: DistrictDatastore
) {
    routing {
        route("/details") {
            // City Details Page
            get("/city/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val city = cityDatastore.read(id)
                    if (city != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            cityDetailsPage(city)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "City not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid City ID")
                }
            }

            // Unit Details Page
            get("/unit/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val unit = unitDatastore.read(id)
                    if (unit != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            unitDetailsPage(unit)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Unit not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Unit ID")
                }
            }

            // Building Details Page
            get("/building/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val building = buildingDatastore.read(id)
                    if (building != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            buildingDetailsPage(building)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Building not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Building ID")
                }
            }

            // District Details Page
            get("/district/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    val district = districtDatastore.read(id)
                    if (district != null) {
                        call.respondHtml(HttpStatusCode.OK) {
                            districtDetailsPage(district)
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "District not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid District ID")
                }
            }
        }
    }
}

fun HTML.cityDetailsPage(city: ExposedCity) {
    head {
        title { +"City Details - ${city.name}" }
        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
    }
    body {
        div("container mt-5") {
            div("card") {
                div("card-header") { +"City: ${city.name}" }
                div("card-body") {
                    p { +"City ID: ${city.id}" }
                    p { +"Player ID: ${city.playerId}" }
                    p { +"Population: ${city.population}" }
                }
            }
        }
    }
}

fun HTML.unitDetailsPage(unit: ExposedUnit) {
    head {
        title { +"Unit Details - ${unit.damage}" }
        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
    }
    body {
        div("container mt-5") {
            div("card") {
                div("card-header") { +"Unit: ${unit.name}" }
                div("card-body") {
                    p { +"Unit ID: ${unit.id}" }
                    p { +"Player ID: ${unit.playerId}" }
                    p { +"Damage: ${unit.damage}" }
                    p { +"Health: ${unit.health}" }
                    p { +"Movement: ${unit.movement}" }
                    p { +"Production Cost: ${unit.productionCost}" }
                    p { +"Salary: ${unit.salary}" }
                    p { +"Description: ${unit.description}" }
                }
            }
        }
    }
}

fun HTML.buildingDetailsPage(building: ExposedBuilding) {
    head {
        title { +"Building Details - ${building.name}" }
        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
    }
    body {
        div("container mt-5") {
            div("card") {
                div("card-header") { +"Building: ${building.name}" }
                div("card-body") {
                    p { +"Building ID: ${building.id}" }
                    p { +"City ID: ${building.cityId}" }
                    p { +"District ID: ${building.districtId}" }
                    p { +"Production: ${building.production}" }
                    p { +"Production Cost: ${building.productionCost}" }
                    p { +"Food: ${building.food}" }
                    p { +"Gold: ${building.gold}" }
                    p { +"Defense: ${building.defense}" }
                    p { +"Description: ${building.description}" }
                }
            }
        }
    }
}

fun HTML.districtDetailsPage(district: ExposedDistrict) {
    head {
        title { +"District Details - ${district.name}" }
        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
    }
    body {
        div("container mt-5") {
            div("card") {
                div("card-header") { +"District: ${district.name}" }
                div("card-body") {
                    p { +"District ID: ${district.id}" }
                    p { +"Name: ${district.name}" }
                    p { +"City ID: ${district.cityId}" }
                    p { +"Production Cost: ${district.productionCost}" }
                }
            }
        }
    }
}


